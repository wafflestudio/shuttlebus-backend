package com.wafflestudio.shattlebus.common

object Utils {
    fun <T> Map<T, Any>.safeGet(idx: T) =
        this[idx]
}
