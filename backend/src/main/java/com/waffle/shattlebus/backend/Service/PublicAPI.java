package com.waffle.shattlebus.backend.Service;
import com.waffle.shattlebus.backend.Data.BusStationInfo;
import com.waffle.shattlebus.backend.Exception.NotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.waffle.shattlebus.backend.Search.SearchHangul.compWord;

@Service
public class PublicAPI {

    String key = "k4UvnK2anWmh10%2BJiof8w7qWin6wmp72vRlUryHNKxrpQ5%2Fot599PY929AaGnv8KpuBh9%2FN0xe2%2F53ja9cgI6g%3D%3D";

    public static String getStations(String id, String path) throws Exception{

        Map<String, List<String>> BSI = BusStationInfo.getStationsAsMap();
        try {
            List<String> Info = BSI.get(id);
            JSONObject apiResponse = getJSON(path);
            if(apiResponse.getJSONObject("msgHeader").getInt("headerCd")==4) throw new NotFoundException("st");
            JSONObject data = apiResponse.getJSONObject("msgBody");
            JSONObject response = new JSONObject();

            response.put("id", id.toString());
            response.put("name", Info.get(0));
            response.put("direction_rep", Info.get(1));

            JSONArray busList = data.getJSONArray("itemList");
            JSONArray ourList = new JSONArray();
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

    public static String getBuses(String id, String path) throws Exception {

        JSONObject response = PublicAPI.getJSON(path);
        if(response.getJSONObject("msgHeader").getInt("headerCd")==4) throw new NotFoundException("bus");
        JSONObject data = response.getJSONObject("msgBody");
        JSONArray  datalist = data.getJSONArray("itemList");

        JSONArray  stations = new JSONArray();
        JSONArray  operating_buses = new JSONArray();
        JSONObject result = new JSONObject();

        for (int i = 0; i < datalist.length(); i++) {

            JSONObject element = (JSONObject) datalist.get(i);

            JSONObject eachStop = new JSONObject();
            eachStop.put("id", element.get("stId"));
            eachStop.put("name", element.get("stNm"));
            eachStop.put("idx", element.get("staOrd"));
            stations.put(eachStop);

            String arrMsg = element.get("arrmsg1").toString();
            if( (arrMsg.contains("0번째") && !arrMsg.contains("10번째")) || arrMsg.contains("곧 도착")){  // ************* note
                JSONObject bus = new JSONObject();
                bus.put("station_id", element.get("stId"));
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

        return result.toString();
    }

    public static String findStations(String query){

        JSONObject response = new JSONObject();
        response.put("result", search_SH(query, true));

        return response.toString();
    }

    public static String getData(String query){

        JSONArray list = new JSONArray();
        list = search_SH(query, true);
        for( Object o: search_SH(query, false)){
            list.put(o);
        }

        return list.toString();
    }

    // 검색 기능 by 승한
    private static JSONArray search_SH(String query, boolean isStation){

        String[] queries = query.split(" ");

        // 검색 처리

        List<List<String>> rev;
        Map<String, List<String>> result_st = new HashMap<>();
        Map<String, String> result_bus = new HashMap<>();

        if(isStation)
            rev = BusStationInfo.getStations();
        else
            rev = BusStationInfo.getBuses();


        for (List<String> info : rev) {
            String name = info.get(1);
            boolean flag = true;
            int st = 0;
            for (String s : queries) {
                st = compWord(name.substring(st), s);
                if (st == -1) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                if(isStation) result_st.put(info.get(0), info.subList(1, 4));
                else result_bus.put(info.get(1), info.get(0));
            }
        }


        JSONArray jArray = new JSONArray();
        if(isStation) {
            for (String key : result_st.keySet()) {
                JSONObject sObject = new JSONObject();
                sObject.put("type", "station");
                sObject.put("id", key);
                sObject.put("name", result_st.get(key).get(0));
                sObject.put("to", result_st.get(key).get(1));
                sObject.put("dir", Integer.parseInt(result_st.get(key).get(2)));
                jArray.put(sObject);
            }
        }
        else{
            for (String key : result_bus.keySet()) {
                JSONObject sObject = new JSONObject();
                sObject.put("type", "bus");
                sObject.put("id", result_bus.get(key));
                sObject.put("name", key);
                jArray.put(sObject);
            }
        }
        return jArray;
    }


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

}
