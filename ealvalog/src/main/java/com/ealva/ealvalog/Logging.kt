/*
 * Copyright 2017 Eric A. Snell
 *
 * This file is part of eAlvaLog.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package com.ealva.ealvalog

import kotlin.reflect.KClass

/**
 * Created by Eric A. Snell on 8/8/18.
 */
fun <T : Any> logger(forClass: Class<T>, marker: Marker? = null): Logger {
    return Loggers[forClass.name, marker]
}

fun <T : Any> lazyLogger(forClass: KClass<T>, marker: Marker? = null): Lazy<Logger> {
    return lazy { logger(forClass, marker) }
}

fun <T : Any> logger(forClass: KClass<T>, marker: Marker? = null): Logger {
    return logger(forClass.java, marker)
}

fun <T : Any> T.logger(marker: Marker? = null): Logger {
    return logger(this.javaClass, marker)
}

fun <T : Any> T.lazyLogger(marker: Marker? = null): Lazy<Logger> {
    return lazy { logger(this.javaClass, marker) }
}

operator fun LogRecordBuilder.invoke(format: String): LogRecordBuilder {
    append(format)
    return this
}

operator fun LogRecordBuilder.invoke(format: String, vararg args: Any): LogRecordBuilder {
    append(format, *args)
    return this
}

operator fun LogRecordBuilder.unaryPlus() {
    addLocation(1)
}

inline fun Logger.t(
    throwable: Throwable? = null,
    marker: Marker? = null,
    block: (LogRecordBuilder) -> Unit
) {
    if (isLoggable(LogLevel.TRACE, marker, throwable)) {
        ExtLogRecord[LogLevel.TRACE, name, marker, throwable].use { record ->
            block(record)
            logImmediate(record)
        }
    }
}

inline fun Logger.d(
    throwable: Throwable? = null,
    marker: Marker? = null,
    block: (LogRecordBuilder) -> Unit
) {
    if (isLoggable(LogLevel.DEBUG, marker, null)) {
        ExtLogRecord[LogLevel.DEBUG, name, marker, throwable].use { record ->
            block(record)
            logImmediate(record)
        }
    }
}

inline fun Logger.i(
    throwable: Throwable? = null,
    marker: Marker? = null,
    block: (LogRecordBuilder) -> Unit
) {
    if (isLoggable(LogLevel.INFO, marker, throwable)) {
        ExtLogRecord[LogLevel.INFO, name, marker, throwable].use { record ->
            block(record)
            logImmediate(record)
        }
    }
}

inline fun Logger.w(
    throwable: Throwable? = null,
    marker: Marker? = null,
    block: (LogRecordBuilder) -> Unit
) {
    if (isLoggable(LogLevel.WARN, marker, null)) {
        ExtLogRecord[LogLevel.WARN, name, marker, throwable].use { record ->
            block(record)
            logImmediate(record)
        }
    }
}

inline fun Logger.e(
    throwable: Throwable? = null,
    marker: Marker? = null,
    block: (LogRecordBuilder) -> Unit
) {
    if (isLoggable(LogLevel.ERROR, marker, null)) {
        ExtLogRecord[LogLevel.ERROR, name, marker, throwable].use { record ->
            block(record)
            logImmediate(record)
        }
    }
}

inline fun Logger.wtf(
    throwable: Throwable? = null,
    marker: Marker? = null,
    block: (LogRecordBuilder) -> Unit
) {
    if (isLoggable(LogLevel.CRITICAL, marker, throwable)) {
        ExtLogRecord[LogLevel.CRITICAL, name, marker, throwable].use { record ->
            block(record)
            logImmediate(record)
        }
    }
}

