package com.wafflestudio.shattlebus.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass

inline fun <reified T : Any> JSONArray.convertTo(type: KClass<T>): List<T> {
    val objectMapper = ObjectMapper()
    return this.map { json ->
        objectMapper.readValue(json.toString(), type.java)
    }
}

inline fun <reified T : Any> JSONObject.convertTo(type: KClass<T>): T {
    val objectMapper = ObjectMapper()
    return objectMapper.readValue(toString(), type.java)
}
