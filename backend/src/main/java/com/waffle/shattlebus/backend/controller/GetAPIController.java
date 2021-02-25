package com.waffle.shattlebus.backend.controller;
import com.waffle.shattlebus.backend.model.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1") // localhost:8080/api
public class GetAPIController {

    List<Station> stationList;
    List<Bus> busList;

    // 정류장 상세
    @GetMapping("/stations/{stationid}")  // localhost:8080/api/getParameters?id=shlee0882&email=shlee0882@gmail.com
    public String getStations(@PathVariable("stationid") Long id){

        // 정류장 db 사용 여부 ??


        return "정류장 id 상세: " + id;
    }

    // 버스 상세
    @GetMapping("/buses/{busid}")
    public String getMultiParameters(@PathVariable("busid") Long id) {

        // 버스 db 사용 여부 ??

        return "버스 id 상세: " + id;
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

}


