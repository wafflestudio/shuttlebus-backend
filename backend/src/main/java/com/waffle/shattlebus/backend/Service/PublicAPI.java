package com.waffle.shattlebus.backend.Service;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.waffle.shattlebus.backend.Data.BusStationInfo;
import com.waffle.shattlebus.backend.Exception.NotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.attribute.standard.JobName;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.waffle.shattlebus.backend.Search.SearchHangul.compWord;
import static com.waffle.shattlebus.backend.keys.*;

@Service
public class PublicAPI {

    static String keys[] = getKeys;
    static int keyA = 0;
    static int keyB = 0;

    // Parse XML to JSON
    public static JSONObject getJSON(String path) throws Exception {

        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.getResponseCode();
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine="";
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return (JSONObject) (XML.toJSONObject(response.toString())).get("ServiceResult");
    }

    public static JSONObject request(String path, boolean isStation) throws Exception {
        JSONObject response = null;
        String finalPath = null;
        int sz = getKeys.length;

        for(int i=0; i<sz; i++) {
            finalPath =  path + (isStation ? keys[keyA] : keys[keyB]);

            response = getJSON(finalPath);

            if (response.getJSONObject("msgHeader").getInt("headerCd") == 7 &&
                    response.getJSONObject("msgHeader").getString("headerMsg").compareTo("Key인증실패: LIMITED NUMBER OF SERVICE REQUESTS EXCEEDS ERROR.[인증모듈 에러코드(22)]") == 0) {
                if (isStation) keyA = (keyA + 1) % sz;
                else keyB = (keyB + 1) % sz;
            }
            else break;
        }
        return response;
    }

    private static JSONArray shuttleArr(String id, String dir) {
        List<ArrayList<String>> stations = BusStationInfo.getShuttleStations();

        JSONArray busList = new JSONArray();
        for (ArrayList<String> info : stations) {
            boolean flag = false;
            for (int i = 2; i < info.size(); i++)
                if (info.get(i).compareTo(id) == 0) {
                    flag = true;
                    break;
                }
            if (!flag) continue;
            JSONObject ele = new JSONObject();
            ele.put("id", info.get(0));
            ele.put("name", info.get(1));
            ele.put("is_shuttle", true);
            ele.put("arrival_time", "");
            ele.put("direction", dir);
            busList.put(ele);
        }
        return busList;
    }

    public static String getShuttleStations(String id) throws Exception{
        Map<String, List<String>> BSI = BusStationInfo.getStationsAsMap();

        try {
            List<String> Info = BSI.get(id);
            JSONObject response = new JSONObject();

            response.put("id", id);
            response.put("name", Info.get(0));
            response.put("direction_rep", Info.get(1));
            response.put("buses", shuttleArr(id, Info.get(1)));

            response.put("latitude", Info.get(3));
            response.put("longitude", Info.get(4));


            return response.toString();
        }
        catch(NullPointerException e){
            throw new NotFoundException("st"); // 존재하지 않는 버스일 경우
        }
    }

    public static String getStations(String id, String path) throws Exception{
        Map<String, List<String>> BSI = BusStationInfo.getStationsAsMap();
        List<ArrayList<String>> stations = BusStationInfo.getShuttleStations();

        try {
            List<String> Info = BSI.get(id);
            JSONObject apiResponse = request(path, true);
            if(apiResponse.getJSONObject("msgHeader").getInt("headerCd")==4) throw new NotFoundException("st");
            JSONObject data = apiResponse.getJSONObject("msgBody");
            JSONObject response = new JSONObject();

            response.put("id", id.toString());
            response.put("name", Info.get(0));
            response.put("direction_rep", Info.get(1));

            JSONArray busList = new JSONArray();
            try {
                busList = data.getJSONArray("itemList");
            } catch (JSONException e){
                JSONObject item = data.getJSONObject("itemList");
                busList.put(item);
            }

            response.put("longitude", ((JSONObject)busList.get(0)).get("gpsX").toString());
            response.put("latitude", ((JSONObject)busList.get(0)).get("gpsY").toString());

            JSONArray ourList = shuttleArr(id, Info.get(1));
            for (int i = 0; i < busList.length(); i++) {
                JSONObject a = (JSONObject) busList.get(i);
                JSONObject n = new JSONObject();
                n.put("direction", a.get("nxtStn").toString());
                n.put("name", a.get("rtNm").toString());
                n.put("id", a.get("busRouteId").toString());
                n.put("arrival_time", a.get("arrmsg1").toString());
                n.put("is_shuttle", false);
                ourList.put(n);
            }

            response.put("buses", ourList);

            return response.toString();
        }
        catch(NullPointerException e){
            throw new NotFoundException("st"); // 존재하지 않는 버스일 경우
        }
    }

    // 시작, 종점.
    private static String getRange(String id) {
        if (id.equals("100100250")) return "서울대입구역,제2공학관";
        if (id.equals("100100251")) return "서울대입구역,제2공학관";
        if (id.equals("100100253")) return "신림2동차고지,제2공학관";
        if (id.equals("120900008")) return "낙성대역,제2공학관";
        return "";
    }



    public static String getBuses(String id, String path) throws Exception {
        JSONObject response = PublicAPI.request(path, false);
        if(response.getJSONObject("msgHeader").getInt("headerCd")==4) throw new NotFoundException("bus");
        JSONObject data = response.getJSONObject("msgBody");
        JSONArray  datalist = data.getJSONArray("itemList");

        JSONArray  stations = new JSONArray();
        JSONArray  operating_buses = new JSONArray();
        JSONObject result = new JSONObject();

        for (int i = 0; i < datalist.length(); i++) {

            JSONObject element = (JSONObject) datalist.get(i);

            JSONObject eachStop = new JSONObject();
            eachStop.put("id", element.get("arsId"));
            eachStop.put("name", element.get("stNm"));
            eachStop.put("idx", element.get("staOrd"));
            stations.put(eachStop);

            String arrMsg = element.get("arrmsg1").toString();
            if( (arrMsg.contains("0번째") && !arrMsg.contains("10번째")) || arrMsg.contains("곧 도착")){  // ************* note
                JSONObject bus = new JSONObject();
                bus.put("station_id", element.get("arsId"));
                bus.put("arriving_at", element.get("stNm"));
                bus.put("plateNo", element.get("plainNo1"));
                operating_buses.put(bus);
            }

            if(i==1){
                result.put("id", element.get("busRouteId"));
                result.put("name", element.get("rtNm"));
                result.put("is_shuttle", false);
                result.put("allocation", element.get("term"));
                result.put("first_bus", element.get("firstTm"));
                result.put("last_bus", element.get("lastTm"));
            }
        }

        result.put("operating_buses", operating_buses);
        result.put("stations", stations);

        String range = getRange(id);
        result.put("range", range);

        return result.toString();
    }

    public static String getShuttleBuses(String id) throws Exception {
        Map<String, List<String>> BSI = BusStationInfo.getStationsAsMap();
        List<ArrayList<String>> shuttle = BusStationInfo.getShuttleBuses();
        List<ArrayList<String>> stations = BusStationInfo.getShuttleStations();

        for(List<String> info : shuttle){
            if(info.get(0).compareTo(id) != 0) continue;

            JSONObject result = new JSONObject();
            result.put("id", id);
            result.put("name", info.get(1));
            result.put("is_shuttle", true);
            result.put("operating_section", info.get(2));
            result.put("time_required", info.get(3).compareTo(" ") == 0 ? null : info.get(3));
            JSONArray dispatch_interval = new JSONArray();
            for(int i=4; i<info.size(); i+=2){
                JSONObject ele = new JSONObject();
                ele.put("time", info.get(i));
                ele.put("interval", info.get(i+1));
                dispatch_interval.put(ele);
            }
            result.put("dispatch_interval", dispatch_interval);
            for(List<String> st : stations){
                if(st.get(0).compareTo(id) != 0) continue;
                int idx = 0;
                JSONArray stationinfo = new JSONArray();
                try {
                    for (int i = 2; i < st.size(); i++) {
                        JSONObject ele = new JSONObject();
                        ele.put("idx", idx++);
                        ele.put("id", st.get(i));
                        ele.put("name", BSI.get(st.get(i)).get(0));
                        stationinfo.put(ele);
                    }
                }
                catch(NullPointerException e){
                    throw new NotFoundException(id); // 존재하지 않는 버스일 경우
                }
                result.put("stations", stationinfo);
                break;
            }
            return result.toString();
        }
        throw new NotFoundException("bus");
    }

    public static String findStations(String query, String tag){

        JSONObject response = new JSONObject();
        response.put("result", search_SH(query, tag, true));

        return response.toString();
    }

    public static String getData(String query, String tag){

        JSONObject response = new JSONObject();
        JSONArray station_list = new JSONArray();
        JSONArray bus_list = new JSONArray();

        station_list = search_SH(query, tag, true);
        bus_list = search_SH(query, tag, false);

        response.put("station_result", station_list);
        response.put("bus_result", bus_list);

        return response.toString();
    }

    // 한번에 다 갖다주기
    public static String getAllStations(){
        return BusStationInfo.getAllStations();
    }


    // 검색 기능
    private static JSONArray search_SH(String query, String tag, boolean isStation){
        // 검색 처리
        if(tag != null && !isStation)
            return new JSONArray();

        List<ArrayList<String>> rev;
        Map<String, List<String>> result_st = new HashMap<>();
        Map<String, List<String>> result_bus = new HashMap<>();

        if(isStation)
            rev = BusStationInfo.getStations();
        else
            rev = BusStationInfo.getBuses();

        for (List<String> info : rev) {
            String name = info.get(1);
            boolean flag = true;
            int st = 0;

            if(query != null) {
                String[] queries = query.split(" ");
                for (String s : queries) {
                    st = compWord(name.substring(st), s);
                    if (st == -1) {
                        flag = false;
                        break;
                    }
                }
            }
            if(tag != null)
                flag &= (info.get(6).compareTo(tag) == 0);

            if (flag) {
                if(isStation) result_st.put(info.get(0), info.subList(1, 4));
                else result_bus.put(info.get(0), info.subList(1, 3));
            }
        }

        JSONArray jArray = new JSONArray();
        if(isStation) {
            for (String key : result_st.keySet()) {
                JSONObject sObject = new JSONObject();
                sObject.put("id", key);
                sObject.put("name", result_st.get(key).get(0));
                sObject.put("to", result_st.get(key).get(1));
                int type = Integer.parseInt(result_st.get(key).get(2));
                sObject.put("type", type == 4 ? 3 : type);
                jArray.put(sObject);
            }
        }
        else{
            for (String key : result_bus.keySet()) {
                JSONObject sObject = new JSONObject();
                sObject.put("id", key);
                sObject.put("name", result_bus.get(key).get(0));
                sObject.put("type", Integer.parseInt(result_bus.get(key).get(1)));
                jArray.put(sObject);
            }
        }
        return jArray;
    }
}
