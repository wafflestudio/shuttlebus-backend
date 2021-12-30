package com.wafflestudio.shattlebus.services.publicApi

import com.wafflestudio.shattlebus.services.publicApi.PublicApiElements.HEADER_CODE
import com.wafflestudio.shattlebus.services.publicApi.PublicApiElements.HEADER_MSG
import com.wafflestudio.shattlebus.services.publicApi.PublicApiElements.RESPONSE_BODY
import com.wafflestudio.shattlebus.services.publicApi.PublicApiElements.RESPONSE_HEADER
import com.wafflestudio.shattlebus.services.publicApi.PublicApiElements.RESPONSE_ITEM_LIST
import com.wafflestudio.shattlebus.services.publicApi.PublicApiElements.SERVICE_RESULT
import com.wafflestudio.shattlebus.services.publicApi.PublicApiElements.THROTTLED_MSG
import org.json.JSONArray
import org.json.JSONObject
import org.json.XML

open class PublicApiResponse(
    private val data: JSONObject
) {
    private val resultBody: JSONObject
        get() = data.getJSONObject(SERVICE_RESULT)

    val headerCode: String
        get() = resultBody.getJSONObject(RESPONSE_HEADER)[HEADER_CODE].toString()

    val headerMsg: String
        get() = resultBody.getJSONObject(RESPONSE_HEADER)[HEADER_MSG].toString()

    val itemList: JSONArray
        get() = resultBody.getJSONObject(RESPONSE_BODY).getJSONArray(RESPONSE_ITEM_LIST)
}

fun PublicApiResponse.isKeyThrottled(): Boolean =
    headerCode == "7" && headerMsg == THROTTLED_MSG

fun PublicApiResponse.isError(): Boolean =
    headerCode in listOf("4", "5")

fun PublicApiResponse.throwOnError(exc: Exception): PublicApiResponse =
    if (isError()) throw exc else this

fun PublicApiResponse(response: String): PublicApiResponse =
    PublicApiResponse(XML.toJSONObject(response))
