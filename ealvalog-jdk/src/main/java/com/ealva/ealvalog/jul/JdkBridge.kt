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

package com.ealva.ealvalog.jul

import com.ealva.ealvalog.FilterResult
import com.ealva.ealvalog.FilterResult.DENY
import com.ealva.ealvalog.LogEntry
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.core.Bridge
import com.ealva.ealvalog.core.CoreLogger
import com.ealva.ealvalog.core.ExtLogRecord
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import java.util.logging.Handler

/**
 * Bridge the [CoreLogger] to the underlying java.util.logging.Logger
 *
 *
 * Created by Eric A. Snell on 3/7/17.
 */
class JdkBridge internal constructor(
  name: String,
  private var filter: LoggerFilter = AlwaysNeutralFilter,
  handler: Handler? = null,
  logLevel: LogLevel? = null
) : Bridge {
  @field:Volatile var parent: JdkBridge? = null  // root bridge will have a null parent
  private val jdkLogger: java.util.logging.Logger =
    java.util.logging.Logger.getLogger(name).apply {
      handler?.let { addHandler(it) }
      level = logLevel?.jdkLevel
    }

  override var logLevel: LogLevel
    get() = LogLevel.fromLevel(jdkLogger.level)
    set(logLevel) {
      jdkLogger.level = logLevel.jdkLevel
    }

  override var includeLocation: Boolean = false

  override var logToParent: Boolean
    get() = jdkLogger.useParentHandlers
    set(value) { jdkLogger.useParentHandlers = value }

  override val name: String
    get() = jdkLogger.name

  override fun getFilter(): LoggerFilter {
    return filter
  }

  override fun setFilter(filter: LoggerFilter?) {
    this.filter = filter ?: AlwaysNeutralFilter
  }

  override fun shouldIncludeLocation(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return includeLocation || parent?.shouldIncludeLocation(level, marker, throwable) == true
  }

  override fun willLogToParent(loggerName: String): Boolean {
    return !bridgeIsLoggerPeer(loggerName) || jdkLogger.useParentHandlers
  }

  /**
   * {@inheritDoc}
   *
   *
   * If the level check passes and any contained filter does not deny, then accepted
   */
  override fun isLoggable(
    loggerName: String,
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    if (!jdkLogger.isLoggable(logLevel.jdkLevel)) {
      return DENY
    }
    return filter.isLoggable(loggerName, logLevel, marker, throwable).acceptIfNeutral()
  }

  override fun log(logEntry: LogEntry) {
    ExtLogRecord.fromLogEntry(logEntry).use { record ->
      jdkLogger.log(record)
    }
  }

  override fun getLevelForLogger(logger: com.ealva.ealvalog.Logger): LogLevel? {
    return if (bridgeIsLoggerPeer(logger.name)) {
      LogLevel.fromLevel(jdkLogger.level)
    } else null
  }

  override fun bridgeIsLoggerPeer(loggerName: String): Boolean {
    return name == loggerName
  }

  fun addLoggerHandler(loggerHandler: Handler) {
    jdkLogger.addHandler(loggerHandler)
  }

  fun setToDefault() {
    jdkLogger.useParentHandlers = true
  }
}
