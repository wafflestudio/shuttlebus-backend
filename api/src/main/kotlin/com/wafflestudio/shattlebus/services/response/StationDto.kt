package com.wafflestudio.shattlebus.services.response

data class StationDto(
    val id: String,
    val name: String,
    val direction_rep: String,
    val shuttles: List<ShuttleBusDto>,
    val cityBuses: List<CityBusDto>?,
    val latitude: String,
    val longitude: String,
) {
    companion object {
        fun of(id: String, stationInfo: List<String>, cityBuses: List<CityBusDto>? = null): StationDto =
            StationDto(
                id = id,
                name = stationInfo[0],
                direction_rep = stationInfo[1],
                shuttles = TODO(),
                cityBuses = cityBuses,
                latitude = stationInfo[3],
                longitude = stationInfo[4],
            )
    }
}
