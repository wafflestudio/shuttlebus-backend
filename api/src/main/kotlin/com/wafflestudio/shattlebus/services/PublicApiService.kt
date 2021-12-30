package com.wafflestudio.shattlebus.services

import com.wafflestudio.shattlebus.common.BusNotFound
import com.wafflestudio.shattlebus.common.PublicApiError
import com.wafflestudio.shattlebus.common.StationNotFound
import com.wafflestudio.shattlebus.common.convertTo
import com.wafflestudio.shattlebus.controllers.StationId
import com.wafflestudio.shattlebus.data.BusStationInfo.stationsAsMap
import com.wafflestudio.shattlebus.services.publicApi.PublicApiResponse
import com.wafflestudio.shattlebus.services.publicApi.throwOnError
import com.wafflestudio.shattlebus.services.response.CityBusDto
import com.wafflestudio.shattlebus.services.response.StationDto
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.DefaultUriBuilderFactory
import java.time.Duration

private const val PUBLIC_API_BASE_URL = "http://ws.bus.go.kr/api/rest"
private const val PUBLIC_API_STATION_INFO = "$PUBLIC_API_BASE_URL/stationinfo/getStationByUid?arsId={id}&serviceKey={key}"

@Service
class PublicApiService {
    private val webClientBuilder = WebClient.builder()
    private val uriBuilderFactory = DefaultUriBuilderFactory(PUBLIC_API_BASE_URL).apply {
        encodingMode = DefaultUriBuilderFactory.EncodingMode.NONE
    }

    var keys = arrayOf(
        "k4UvnK2anWmh10%2BJiof8w7qWin6wmp72vRlUryHNKxrpQ5%2Fot599PY929AaGnv8KpuBh9%2FN0xe2%2F53ja9cgI6g%3D%3D",
        "WSApjS8xA8hmoAiPWLblVzNkdbtpb1RKw1UvSfMfb31VU18ghPiRyDsiSyl1p4Umb9%2BYDzyRAunz6SfDAnvZHQ%3D%3D",
        "%2FMkSz%2BUEH%2Bt7LyuQ%2B3ry95YgcaogASEEYQWNkwYZAQT%2Bk7O5ntS8hfaZ3rUFlQoSlO3DXtEk3ohBMSk8saq0sA%3D%3D",
        "X2JIYquIWkd7%2FnJD5l7lgs2vkTY4EvBsPV8XSj9sGIbZaWL8lZ9Hg931hPLAb8qTrhvdmzcx5GxtVCs60JHcIQ%3D%3D",
        "43VydJSargV5BDnwBWg5yzx34B8hTDCIfezVUNgLAZ6inubKMxBP4RJw9vZdD%2B0HVbWNAQ2CXj%2B8Hufp7%2B6ujA%3D%3D"
    )
    var stationKey = 0
    var spareKey = 0

    fun getStations(stationId: StationId): StationDto {
        val id = stationId.value
        val busStationInfo = stationsAsMap[id] ?: throw StationNotFound()
        val cityBuses = if (!stationId.isShuttleBusStation()) {
            getCityBuses(id)
        } else null
        return StationDto(id, busStationInfo, cityBuses)
    }

    /**
     *  if (
     *    response.getJSONObject("msgHeader").getInt("headerCd") === 7 &&\
     *    response.getJSONObject("msgHeader").getString("headerMsg").compareTo(
     *      "Key인증실패: LIMITED NUMBER OF SERVICE REQUESTS EXCEEDS ERROR.[인증모듈 에러코드(22)]"
     *    ) === 0
     *  ) { if (isStation) keyA = (keyA + 1) % 5 else keyB = (keyB + 1) % 5 } else break
     */
    fun getCityBuses(stationId: String): List<CityBusDto> {
        val response = webClientBuilder
            .build()
            .get()
            .uri(
                // request URL
                uriBuilderFactory
                    .uriString(PUBLIC_API_STATION_INFO)
                    .build(stationId, keys[stationKey])
            )
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            .acceptCharset(Charsets.UTF_8)
            .retrieve()
            .bodyToMono(String::class.java)
            .block(Duration.ofSeconds(1))
            ?.let(::PublicApiResponse)
            ?.throwOnError(BusNotFound())
            ?: throw PublicApiError(HttpStatus.CONFLICT, "다시 시도해주세요.")

        val items = response.itemList
        return items.convertTo(CityBusDto::class)
    }
}
