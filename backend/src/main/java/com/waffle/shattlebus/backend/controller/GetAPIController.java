package com.waffle.shattlebus.backend.controller;
import com.waffle.shattlebus.backend.model.*;
import org.json.*;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.JSONObject;
import org.json.XML;

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

    // 정류장 상세 - 자체 리스트
    @GetMapping("/stations/{stationid}")
    public String getStations(@PathVariable("stationid") Long id) throws Exception{

        String path = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?serviceKey="
                + key
                + "&arsId=" + id;

        //List<List<String>> Info = BusTsvInfo.getBusTsvInfo();
        Map<Long, List<String>> BTI = BusTsvInfo.getBTIMap();
        List<String> Info = BTI.get(id);
        JSONObject data = (JSONObject) getJSON(path).get("msgBody");
        JSONObject response = new JSONObject();

        response.put("id", id.toString());
        response.put("name", Info.get(0));
        response.put("direction", Info.get(1));

        // 버스 리스트 받아오기
        JSONArray busList = data.getJSONArray("itemList"); // 여기서 정보 추출 필요
        JSONArray ourList = new JSONArray();

        response.put("buses", busList);
        // arrmsg1, rtNm, busRouteId -> 따로 파싱을 해야하나 어떻게 해야하나

        return response.toString();
    }

    // 버스 상세 - 자체 리스트
    @GetMapping("/buses/{busid}")
    public JSONObject getBuses(@PathVariable("busid") String id) throws Exception {
        
        String path = "http://ws.bus.go.kr/api/rest/busRouteInfo/getRoutePath?serviceKey="
        + key + "&busRouteId=" + id;

        return getJSON(path);
    }

    // 정류장 검색
    @GetMapping("/stations") 
    public String getStations(@RequestBody String station){

        // 검색 처리

        return "정류장 이름: " + station;
    }

    // 통합 검색
    @GetMapping("/find")
    public String getData(@RequestBody String data){

        // 검색 처리

        return "정류장 또는 버스 이름: " + data;
    }

    //길찾기
    @GetMapping("/findpath/")
    public String getListPath(@RequestParam String from, @RequestParam String to) {

        // 검색 처리

        return "길찾기 결과: " + from + " & " + to;

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
}


