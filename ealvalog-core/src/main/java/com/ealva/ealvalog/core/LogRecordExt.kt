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

package com.ealva.ealvalog.core

import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import java.util.logging.LogRecord

/**
 * Created by Eric A. Snell on 8/16/18.
 */
val LogRecord.logLevel: LogLevel
    get() = (this as? ExtLogRecord)?.logLevel ?: LogLevel.fromLevel(
        level
    )

val LogRecord.marker: Marker?
    get() = (this as? ExtLogRecord)?.marker

//fun LogRecord.isPublishedAt(level: Level): Boolean {
//    val jdkLevel = level.intValue()
//    return this.level.intValue() >= jdkLevel && jdkLevel != Level.OFF.intValue()
//}

fun LogRecord.shouldBePublished(filter: LoggerFilter): Boolean {
    return filter.isLoggable(loggerName, logLevel, marker, thrown).shouldProceed
}

