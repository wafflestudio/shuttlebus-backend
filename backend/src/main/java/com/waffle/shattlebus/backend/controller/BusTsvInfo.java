package com.waffle.shattlebus.backend.controller;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

        /*

        tsv로 작성

        ==================================================================
        ars-id          정류장 id
        stationName	    정류장 이름
        to	            방면                                              * 여기까지가 정류장 정보
        dir             방향 (1: 시계 / 2: 반시계 / 3: 등산 / 4: 하산)*
        busName         버스 이름
        note            특이사항                                           * 버스 관련 정보
        ==================================================================

        *dir 추가설명 1> 관악02는 전부 3, 4번만 사용.
        *dir 추가설명 2> 5511, 5513, 5516은 서울대학교 혹은 서울대학교 정문 역을 기준으로 3, 4번을 사용.
        *note 추가설명 1> 5511이 8자로 꺾이는 부분은 '8'로 표시.
        *note 추가설명 2> 5516의 경우 첫 번째 바퀴는 '1', 두 번째 바퀴는 '2'로 표시.

         */


public class BusTsvInfo {

    static List<List<String>> getBusTsvInfo() {

        List<List<String>> busInfoList = new ArrayList<>();

        try {
            String tsvPath = "src/main/java/com/waffle/shattlebus/backend/controller/BusTsvInfo.tsv";

            BufferedReader br = new BufferedReader(new FileReader(tsvPath));
            String line = "";
            while((line = br.readLine())!=null) {
                String[] eachLineSplit = line.split("\t");
                busInfoList.add(Arrays.asList(eachLineSplit));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
     return busInfoList;
    } // 이중리스트로 넣어놓음.

    static Map<Long, List<String>> getBTIMap() {

        Map<Long, List<String>> busInfoList = new HashMap<>();

        try {
            String tsvPath = "src/main/java/com/waffle/shattlebus/backend/controller/BusTsvInfo.tsv";

            BufferedReader br = new BufferedReader(new FileReader(tsvPath));
            String line = "";
            while((line = br.readLine())!=null) {
                String[] eachLineSplit = line.split("\t");
                // 한줄씩 처리 ㄱㄱ
                List<String> array = Arrays.asList(eachLineSplit);
                busInfoList.put( (long) Integer.parseInt(array.get(0)), array.subList(1,5));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return busInfoList;
    } // 이중리스트로 넣어놓음.
}
    

