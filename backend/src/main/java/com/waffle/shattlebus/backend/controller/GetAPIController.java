package com.waffle.shattlebus.backend.controller;
import com.waffle.shattlebus.backend.Exception.NotFoundException;
import com.waffle.shattlebus.backend.model.*;
import org.json.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONObject;
import org.json.XML;

import static com.waffle.shattlebus.backend.controller.BusTsvInfo.getBusTsvInfo;
import static com.waffle.shattlebus.backend.searching.SearchHangul.compWord;

@RestController
@RequestMapping("/api/v1")
public class GetAPIController {

    String USER_AGENT = "Mozilla/5.0";
    String key = "k4UvnK2anWmh10%2BJiof8w7qWin6wmp72vRlUryHNKxrpQ5%2Fot599PY929AaGnv8KpuBh9%2FN0xe2%2F53ja9cgI6g%3D%3D";

    //usage
    String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>test value</test><test2>test2 value</test2>";
    JSONObject jsonObject = XML.toJSONObject(xmlString);

    List<Station> stationList;
    List<Bus> busList;


    @GetMapping("/stations/{stationid}")
    public String getStations(@PathVariable("stationid") Long id) throws Exception{

        String path = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?serviceKey="
                + key
                + "&arsId=" + id;

        //List<List<String>> Info = BusTsvInfo.getBusTsvInfo();
        Map<Long, List<String>> BTI = BusTsvInfo.getBTIMap();

        // ** 정류장이 존재하지 않는 경우

        try {
            List<String> Info = BTI.get(id);
            JSONObject apiResponse = getJSON(path);
            if(apiResponse.getJSONObject("msgHeader").getInt("headerCd")==4) throw new NotFoundException("st");
            JSONObject data = apiResponse.getJSONObject("msgBody");
            JSONObject response = new JSONObject();

            response.put("id", id.toString());
            response.put("name", Info.get(0));
            response.put("direction", Info.get(1));

            // ***시내*** 버스 리스트 받아오기
            JSONArray busList = data.getJSONArray("itemList"); // 여기서 정보 추출 필요
            JSONArray ourList = new JSONArray();
            for (int i = 0; i < busList.length(); i++) {
                JSONObject a = (JSONObject) busList.get(i);
                JSONObject n = new JSONObject();
                n.put("name", a.get("rtNm").toString());
                n.put("id", a.get("busRouteId").toString());
                n.put("arrival_time", a.get("arrmsg1").toString());
                n.put("is_shuttle", false);
                ourList.put(n);
            }
            response.put("buses", ourList);

            // 셔틀버스 리스트 받아오기 (?)
            // 1. 교내순환
            // 2. 역순환

            return response.toString();
        }
        catch(NullPointerException e){
            // 존재하지 않는 버스일 경우 NullPointerException
            throw new NotFoundException("st");
        }
    }

    //                                2. 버스 상세
    @GetMapping("/buses/{busid}")
    public String getBuses(@PathVariable("busid") String id) throws Exception {
        
        String path = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?serviceKey="
        + key + "&busRouteId=" + id;

        // 일단 못찾는 경우는 배제하도록 하자
        JSONObject response = getJSON(path);
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

    //                             3. 정류장 검색 by 승한
    @GetMapping("/stations") 
    public String getStations(@RequestParam(value = "query", required = false) String query){

        JSONObject response = new JSONObject();
        response.put("result", search_SH(query, true));

        return response.toString();
    }

    //                             4. 통합 검색
    @GetMapping("/find")
    public String getData(@RequestParam(value = "query", required = false) String query){

        JSONObject response = new JSONObject();
        response.put("station_result", search_SH(query, true));
        response.put("bus_result", search_SH(query, false));

        return response.toString();
    }

    //길찾기 - 미구현
    @GetMapping("/findpath/")
    public String getListPath(@RequestParam String from, @RequestParam String to) {

        // 검색 처리

        return "길찾기 결과: " + from + " & " + to;

    }

    @ExceptionHandler(NotFoundException.class)
    public
    ResponseEntity<Object> NotFoundMessage(NotFoundException e){

        JSONObject response = new JSONObject();
        int errCd = 0;
        String msg = "";

        if(e.getMessage().equals("st")){
            errCd = 10001;
            msg = "invalid station id";
        }
        else if(e.getMessage().equals("bus")){
            errCd = 20001;
            msg = "invalid bus id";
        }
        response.put("errorcode", errCd);
        response.put("message", msg);
        return ResponseEntity.badRequest().body(response.toString());
    }

    //부차적인 methods
    public JSONArray search_SH(String query, boolean isStation){

        String[] queries = query.split(" ");

        // 검색 처리

        List<List<String>> rev;
        Map<String, List<String>> result_st = new HashMap<>();
        Map<String, String> result_bus = new HashMap<>();

        if(isStation)
            rev = getBusTsvInfo();
        else
            rev = getBuses();


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
                sObject.put("id", result_bus.get(key));
                sObject.put("name", key);
                jArray.put(sObject);
            }
        }
        return jArray;
    }

    public JSONObject getJSON(String path) throws Exception {

        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.getResponseCode(); // 에러처리 아직 안함
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine="";
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close(); // print result
        return (JSONObject) (XML.toJSONObject(response.toString())).get("ServiceResult");
    }

    static List<List<String>> getBuses() {

        List<List<String>> buses = new ArrayList<>();

        try {
            InputStream inputStream = GetAPIController.class.getResourceAsStream("/Buses.tsv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while((line = br.readLine())!=null) {
                String[] eachLineSplit = line.split("\t");
                buses.add(Arrays.asList(eachLineSplit));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buses;
    }

}


