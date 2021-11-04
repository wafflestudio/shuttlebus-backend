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
    String keys[] = {
        "k4UvnK2anWmh10%2BJiof8w7qWin6wmp72vRlUryHNKxrpQ5%2Fot599PY929AaGnv8KpuBh9%2FN0xe2%2F53ja9cgI6g%3D%3D",
        "WSApjS8xA8hmoAiPWLblVzNkdbtpb1RKw1UvSfMfb31VU18ghPiRyDsiSyl1p4Umb9%2BYDzyRAunz6SfDAnvZHQ%3D%3D",
        "%2FMkSz%2BUEH%2Bt7LyuQ%2B3ry95YgcaogASEEYQWNkwYZAQT%2Bk7O5ntS8hfaZ3rUFlQoSlO3DXtEk3ohBMSk8saq0sA%3D%3D",
        "X2JIYquIWkd7%2FnJD5l7lgs2vkTY4EvBsPV8XSj9sGIbZaWL8lZ9Hg931hPLAb8qTrhvdmzcx5GxtVCs60JHcIQ%3D%3D",
        "tempkey2" // 승한 요거 추가해주세요~~!!
    };
   
    int keyA = 0;
    int keyB = 0;

    public void changekey(int type){ // 0 for get station(Type A), 1 for get bus route(Type B) 
        if (type==0) { keyA+=1; if (keyA==5) keyA-=5;}
        else { keyB+=1; if (keyB==5) keyB-=5; }
    }

	// 읽어주세요
	// 동일한 하나의 키에 대해, [정류장 call 1000번], [버스 call 1000번]이므로 정류장 call을 다 써버렸더라도, 버스 call에는 사용될 수 있습니다.
	// 따라서 keyA(정류장용)와 keyB(버스용)을 따로 보관합니다.
	
	// ex) keyA에서 에러가 발생하면(1000번 다 쓰면) changekey를 호출한 뒤, 다시 리퀘를 날리면 됩니다.
	// 승한님은 리턴하는 것이 호출 횟수 제한 에러인지 확인해주시고, 그렇다면 changekey 호출해서 다시 호출해주시면 됩니다.
	// 해당 에러 코드는 500 internal 에러로 현재 나오고, headerMsg에 "Key 인증 실패"라고 뜹니다.

    //usage
    String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>test value</test><test2>test2 value</test2>";
    JSONObject jsonObject = XML.toJSONObject(xmlString);


    @GetMapping("/stations/{stationid}")
    public String getStations(@PathVariable("stationid") String id) throws Exception{
   
        if(id.compareTo("50000") > 0) return PublicAPI.getShuttleStations(id);

        String path = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?serviceKey="
                + keys[keyA]
                + "&arsId=" + id;
                
        // for test
        // for (int i=0; i<3000; i++) PublicAPI.getStations(id, path);
        
        return PublicAPI.getStations(id, path);
        
        // 리턴벨류 확인, 필요하다면 changekey(0)
        		 
    }

    //                              2-1. 버스 상세
    @GetMapping("/buses/{busid}")
    public String getBuses(@PathVariable("busid") String id) throws Exception {

        String path = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?serviceKey="
                + keys[keyB] + "&busRouteId=" + id;
        return PublicAPI.getBuses(id, path);
         // 리턴벨류 확인, 필요하다면 changekey(0)
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


