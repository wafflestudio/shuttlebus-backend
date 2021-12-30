package com.wafflestudio.shattlebus.controllers

import com.wafflestudio.shattlebus.services.PublicApiService
import com.wafflestudio.shattlebus.services.response.StationDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@JvmInline
value class StationId(val value: String) {
    fun isShuttleBusStation() = value > "50000"
}

@RestController
@RequestMapping("/api/v1")
class PublicApiController(
    private val publicApiService: PublicApiService,
) {
    @GetMapping("/stations/{stationId}")
    fun getStations(
        @PathVariable stationId: String
    ): StationDto {
        return publicApiService.getStations(StationId(value = stationId))
    }
}
