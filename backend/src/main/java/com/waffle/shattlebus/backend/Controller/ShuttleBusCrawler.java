package com.waffle.shattlebus.backend.Controller;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ShuttleBusCrawler {

    //리턴형 결정//

    static void findSbByStation(int station_code) {

        try {
            Document res = Jsoup.connect("http://shuttlebus.snu.ac.kr/mobile/station/stationDetail.action?bus_station_code=" + station_code)
                    .method(Connection.Method.GET)
                    .execute()
                    .parse();

            Element sbText = res.select(".stationViewList").first();
            int sbNum = sbText.select("li").size();
            List<String> eachSb = new ArrayList<>();
            for (int i = 0; i < sbNum; i++) {
                eachSb.add(
                        sbText.getElementsByClass("title").get(i).text() + "/" +
                                sbText.getElementsByClass("biconArea").get(i).text() + "/" +
                                sbText.getElementsByClass("date start").get(i).text()
                );
            }
            for (String v : eachSb) { System.out.println(v + "\n"); }
        } catch (IOException e) { e.printStackTrace(); }
    }

    static void findSbByRoute(int route_code) {

        try {
            Document res = Jsoup.connect("http://shuttlebus.snu.ac.kr/mobile/route/routeStation.action?bus_route_id=" + route_code)
                    .method(Connection.Method.GET)
                    .execute()
                    .parse();

            /* Find "curr_location" tag. Unavailable at this moment. */

        } catch (IOException e) { e.printStackTrace(); }
    }

}
