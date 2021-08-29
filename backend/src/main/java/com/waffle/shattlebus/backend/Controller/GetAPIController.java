package com.waffle.shattlebus.backend.Controller;
import com.waffle.shattlebus.backend.Exception.NotFoundException;
import com.waffle.shattlebus.backend.Data.BusStationInfo;
import com.waffle.shattlebus.backend.Service.PublicAPI;
import org.json.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.JSONObject;
import org.json.XML;

import static com.waffle.shattlebus.backend.Search.SearchHangul.compWord;

@RestController
@RequestMapping("/api/v1")
public class GetAPIController {

    String USER_AGENT = "Mozilla/5.0";
    String key = "k4UvnK2anWmh10%2BJiof8w7qWin6wmp72vRlUryHNKxrpQ5%2Fot599PY929AaGnv8KpuBh9%2FN0xe2%2F53ja9cgI6g%3D%3D";

    //usage
    String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>test value</test><test2>test2 value</test2>";
    JSONObject jsonObject = XML.toJSONObject(xmlString);


    @GetMapping("/stations/{stationid}")
    public String getStations(@PathVariable("stationid") String id) throws Exception{

        if(id.compareTo("50000") > 0) return PublicAPI.getShuttleStations(id);

        String path = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?serviceKey="
                + key
                + "&arsId=" + id;

        return PublicAPI.getStations(id, path);
    }

    //                              2-1. 버스 상세
    @GetMapping("/buses/{busid}")
    public String getBuses(@PathVariable("busid") String id) throws Exception {

        String path = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?serviceKey="
                + key + "&busRouteId=" + id;

        return PublicAPI.getBuses(id, path);
    }

    //                              2-2. 셔틀버스 상세
    @GetMapping("/shuttlebuses/{busid}")
    public String getShuttlebuses(@PathVariable("busid") String id) throws Exception {

        return PublicAPI.getShuttleBuses(id);
    }

    //                             3. 정류장 검색
    @GetMapping("/stations") 
    public String findStations(@RequestParam(value = "query", required = false) String query,
                               @RequestParam(value = "tag", required = false) String tag){

        return PublicAPI.findStations(query, tag);
    }

    //                             4. 통합 검색
    @GetMapping("/find")
    public String getData(@RequestParam(value = "query", required = false) String query,
                          @RequestParam(value = "tag", required = false) String tag){

        return PublicAPI.getData(query, tag);
    }

    //              `              5. 길찾기 - v1에서 미구현
    @GetMapping("/findpath/")
    public String getListPath(@RequestParam String from, @RequestParam String to) {

        // 검색 처리

        return "길찾기 결과: " + from + " & " + to;

    }

    //                              6. station 일괄 받아오기
    @GetMapping("/stationlist")
    public String getstationlist() {
        return PublicAPI.getAllStations();
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

}


