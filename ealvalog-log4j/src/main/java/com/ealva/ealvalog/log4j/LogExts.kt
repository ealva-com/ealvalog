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

package com.ealva.ealvalog.log4j

import com.ealva.ealvalog.LogLevel
import org.apache.logging.log4j.Level
import java.util.EnumMap

/**
 * Created by Eric A. Snell on 8/29/18.
 */

private val levelToLogLevelMap: Map<Level, LogLevel> = HashMap<Level, LogLevel>(8).apply {
  put(Level.OFF, LogLevel.NONE)
  put(Level.FATAL, LogLevel.CRITICAL)
  put(Level.ERROR, LogLevel.ERROR)
  put(Level.WARN, LogLevel.WARN)
  put(Level.INFO, LogLevel.INFO)
  put(Level.DEBUG, LogLevel.DEBUG)
  put(Level.TRACE, LogLevel.TRACE)
  put(Level.ALL, LogLevel.ALL)
}

val Level.logLevel: LogLevel
  get() {
    return levelToLogLevelMap[this] ?: LogLevel.INFO
  }

private val logLevelToLevelMap: Map<LogLevel, Level> =
  EnumMap<LogLevel, Level>(LogLevel::class.java).apply {
    put(LogLevel.NONE, Level.OFF)
    put(LogLevel.CRITICAL, Level.FATAL)
    put(LogLevel.ERROR, Level.ERROR)
    put(LogLevel.WARN, Level.WARN)
    put(LogLevel.INFO, Level.INFO)
    put(LogLevel.DEBUG, Level.DEBUG)
    put(LogLevel.TRACE, Level.TRACE)
    put(LogLevel.ALL, Level.ALL)
  }

val LogLevel.log4jLevel: Level
  get() {
    return logLevelToLevelMap[this] ?: Level.INFO
  }
