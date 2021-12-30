package com.wafflestudio.shattlebus.services.response

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class CityBusDto constructor(
    @JsonProperty("id")
    @JsonAlias("busRouteId")
    val id: String,
    @JsonProperty("direction")
    @JsonAlias("nxtStn")
    val direction: String,
    @JsonProperty("name")
    @JsonAlias("rtNm")
    val name: String,
    @JsonProperty("arrivalTime")
    @JsonAlias("arrmsg1")
    val arrivalTime: String,
)
