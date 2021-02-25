package com.waffle.shattlebus.backend.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NormalBusCrawler {

    private static String getXml(int arsId) throws Exception{
        String USER_AGENT = "Mozilla/5.0";
        String key = "k4UvnK2anWmh10%2BJiof8w7qWin6wmp72vRlUryHNKxrpQ5%2Fot599PY929AaGnv8KpuBh9%2FN0xe2%2F53ja9cgI6g%3D%3D";
        String path = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey=" + key + "&arsId=" + arsId;

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
    private static void findNbByStation(int arsId) throws Exception {

        String entireXml=getXml(arsId);
        Document doc = Jsoup.parse(entireXml);
        System.out.println(doc);

        // 여기 아래에 jsoup으로 크롤링 코드 작성 //

    }
}
