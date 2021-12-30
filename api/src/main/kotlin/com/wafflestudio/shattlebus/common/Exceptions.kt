package com.wafflestudio.shattlebus.common

import org.springframework.http.HttpStatus

abstract class NotFound(val errorCode: Int, message: String = "") : Exception(message)

class StationNotFound(msg: String = "Invalid Station ID") : NotFound(ErrorCodes.INVALID_STATION_ID, msg)
class BusNotFound(msg: String = "Invalid Bus ID") : NotFound(ErrorCodes.INVALID_BUS_ID, msg)

class PublicApiError(val status: HttpStatus, msg: String = "error has occurred while fetching Public API") : Exception(msg)
