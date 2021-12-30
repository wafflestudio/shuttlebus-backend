package com.wafflestudio.shattlebus.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(NotFound::class)
    fun notFound404(e: NotFound): ResponseEntity<Any> =
        ResponseEntity(ErrorResponse(e.errorCode, e.message), HttpStatus.NOT_FOUND)
}

data class ErrorResponse(
    val errorCode: Int,
    val message: String?,
)