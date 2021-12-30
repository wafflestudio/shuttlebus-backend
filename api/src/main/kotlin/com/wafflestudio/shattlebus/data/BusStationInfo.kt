package com.wafflestudio.shattlebus.data

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.util.Arrays
import kotlin.Throws
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*
        TODO JPA 사용하기 .. 이대로는 유지 보수 절대 안됨

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
object BusStationInfo {
    // File객체 생성
    //        System.out.println("Working Directory = " + System.getProperty("user.dir"));
    val allStations: String
        get() {
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
            val filePath = "./src/main/resources/stationsList"
            val file = File(filePath) // File객체 생성
            var line = ""
            try {
                val reader = BufferedReader(FileReader(file))
                line = reader.readLine()
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return line
        }
    val buses: List<ArrayList<String>>
        get() {
            val buses: MutableList<ArrayList<String>> = ArrayList()
            try {
                val inputStream = BusStationInfo::class.java.getResourceAsStream("/Buses.tsv")
                read(buses, inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return buses
        }
    val shuttleBuses: List<ArrayList<String>>
        get() {
            val buses: MutableList<ArrayList<String>> = ArrayList()
            try {
                val inputStream = BusStationInfo::class.java.getResourceAsStream("/shuttleInfo.tsv")
                read(buses, inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return buses
        }
    val shuttleStations: List<ArrayList<String>>
        get() {
            val stations: MutableList<ArrayList<String>> = ArrayList()
            try {
                val inputStream = BusStationInfo::class.java.getResourceAsStream("/shuttleStationInfo.tsv")
                read(stations, inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return stations
        }

    // 이중리스트로 넣어놓음.
    val stations: List<ArrayList<String>>
        get() {
            val busInfoList: MutableList<ArrayList<String>> = ArrayList()
            try {
                val inputStream = BusStationInfo::class.java.getResourceAsStream("/BusTsvInfo.tsv")
                read(busInfoList, inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val tagInfoList: MutableList<ArrayList<String>> = ArrayList()
            try {
                val inputStream = BusStationInfo::class.java.getResourceAsStream("/StationTagsInfo.tsv")
                read(tagInfoList, inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            for (info in busInfoList) {
                var flag = false
                for (tags in tagInfoList) {
                    if (info[0].compareTo(tags[2]) == 0) {
                        info.add(tags[0])
                        flag = true
                        break
                    }
                }
                if (!flag) info.add("")
            }
            return busInfoList
        }

    // 한줄씩 처리 ㄱㄱ
    val stationsAsMap: Map<String, List<String>>
        get() {
            val busInfoList: MutableMap<String, List<String>> = HashMap()
            try {
                val inputStream = BusStationInfo::class.java.getResourceAsStream("/BusTsvInfo.tsv")
                val br = BufferedReader(InputStreamReader(inputStream))
                var line = br.readLine()
                while (line != null) {
                    val eachLineSplit = line.split("\t").toTypedArray()
                    // 한줄씩 처리 ㄱㄱ
                    val array = Arrays.asList(*eachLineSplit)
                    busInfoList[array[0]] = array.subList(1, 6)
                    line = br.readLine()
                }
                br.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return busInfoList
        }

    fun read(buses: MutableList<ArrayList<String>>, inputStream: InputStream?) {
        val br = BufferedReader(InputStreamReader(inputStream))
        var line = ""
        while (br.readLine().also { line = it } != null) {
            val eachLineSplit = line.split("\t").toTypedArray()
            buses.add(ArrayList(Arrays.asList(*eachLineSplit)))
        }
        br.close()
    }
}
