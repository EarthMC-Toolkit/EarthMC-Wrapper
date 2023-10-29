package io.github.emcw.interfaces

import io.github.emcw.utils.GsonUtil

interface ISerializable {
    fun asString(): String? {
        return GsonUtil.serialize<Any>(this)
    }
}