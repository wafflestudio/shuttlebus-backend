package com.waffle.shattlebus.backend.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NormalBusCrawler {


    /*
    xml받아오기 : 인코딩 문제로 jsoup 과 분리하였음.
     */
    private static String getXml(int code, String type) throws Exception {
        String USER_AGENT = "Mozilla/5.0";
        String key = "k4UvnK2anWmh10%2BJiof8w7qWin6wmp72vRlUryHNKxrpQ5%2Fot599PY929AaGnv8KpuBh9%2FN0xe2%2F53ja9cgI6g%3D%3D";
        String path = "";
        if (type == "station") {
            path = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey=" + key + "&arsId=" + code;
        }
        if (type == "bus") {
            path = ""; // 버스 노선 조회 api 링크 가져오기
        }
        URL url = new URL(path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close(); // print result
        return response.toString();

    }


    /*
    정류장 검색
     */
    private static void findStation(int arsId) throws Exception {

        String entireXml = getXml(arsId, "station");
        Document doc = Jsoup.parse(entireXml);
        int len = doc.select("busRouteId").size();
        List<String> eachNb = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            eachNb.add(
                    doc.select("busRouteId").get(i).text() + "/" +
                            doc.select("rtNm").get(i).text() + "/" +
                            doc.select("arrmsg1").get(i).text() + "/" +
                            doc.select("isFullFlag1").get(i).text()
            );
        }
        for (String v : eachNb) {
            System.out.println(v + "\n");
        }
    }

    /*
    버스 검색
    */
    private static void findBusRoute(int bus_code) throws Exception {

        String entireXml = getXml(bus_code, "bus");
        Document doc = Jsoup.parse(entireXml);
        String basic =
                doc.select("busRouteId").first().text() + "/" +
                        doc.select("rtNm").first().text() + "/" +
                        doc.select("term").first().text() + "/" +
                        doc.select("firstTm").first().text() + "/" +
                        doc.select("lastTm").first().text() + "/";

        int len = doc.select("busRouteId").size();

        List<String> eachStation = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            eachStation.add(
                    doc.select("stId").get(i).text() + "/" +
                            doc.select("stNm").get(i).text() + "/"
            );
        }

        List<String> operBus = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            String msg = doc.select("arrmsg1").get(i).text();
//            if (msg.equals("출발대기")) {
//                operBus.add(
//                        doc.select("stNm").get(i).text() + "/" +
//                                doc.select("plainNo1").get(i).text() + "/" +
//                                doc.select("arrmsg1").get(i).text()
//                );
            if (msg.equals("곧 도착")) {
                operBus.add(
                        doc.select("stNm").get(i).text() + "/" +
                                doc.select("plainNo1").get(i).text() + "/" +
                                doc.select("arrmsg1").get(i).text()
                );
            }

        }

        System.out.println(basic);
        System.out.println("##########################################3");
        for (String v : eachStation) {
            System.out.println(v + "\n");
        }

        System.out.println("##########################################3");
        for (String v : operBus) {
            System.out.println(v + "\n");
        }
    }


        /*
        busRouteId
        rtNm
        출발지
        도착지
        term
        firstTm
        lastTm
        dir 방향
        회차지
        stations [
            idx 인덱스
            stId
            stNm
            ]
        operating [
            stationNm1
            stationNm2
            plainNo1
            plainNo2
            탑:x
         */


    // test용 main 코드 //
    public static void main(String[] args) {

        try {
            findStation(21278);
            findBusRoute(120900008);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}