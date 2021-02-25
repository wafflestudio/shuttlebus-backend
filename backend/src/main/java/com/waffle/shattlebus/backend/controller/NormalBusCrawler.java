package com.waffle.shattlebus.backend.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NormalBusCrawler {

    private static String getXml(int code, String type) throws Exception{ // 리퀘 받아오는 코드 (인코딩 문제로 jsoup과 분리.)
        String USER_AGENT = "Mozilla/5.0";
        String key = "k4UvnK2anWmh10%2BJiof8w7qWin6wmp72vRlUryHNKxrpQ5%2Fot599PY929AaGnv8KpuBh9%2FN0xe2%2F53ja9cgI6g%3D%3D";
        String path = "";
        if (type=="station") {
            path = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey=" + key + "&arsId=" + code;
        }
        if (type=="bus") {
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
    private static void findStation(int arsId) throws Exception {

        String entireXml=getXml(arsId, "station");
        Document doc = Jsoup.parse(entireXml);
        System.out.println(doc);
        // 여기 아래에 jsoup으로 크롤링 코드 작성 //

    }
    private static void findBusRoute(int bus_code) throws Exception {

        String entireXml=getXml(bus_code, "station");
        Document doc = Jsoup.parse(entireXml);
        System.out.println(doc);
        // 여기 아래에 jsoup으로 크롤링 코드 작성 //
        
    }
}
