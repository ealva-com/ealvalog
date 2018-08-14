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

package com.ealva.ealvalog.impl

import com.ealva.ealvalog.FilterResult
import com.ealva.ealvalog.FilterResult.DENY
import com.ealva.ealvalog.FilterResult.NEUTRAL
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.NullMarker
import com.ealva.ealvalog.core.Bridge
import com.ealva.ealvalog.core.CoreLogger
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import com.ealva.ealvalog.util.NullThrowable
import java.util.logging.Handler
import java.util.logging.LogRecord

/**
 * Bridge the [CoreLogger] to the underlying java.util.logging.Logger
 *
 *
 * Created by Eric A. Snell on 3/7/17.
 */
class JdkBridge internal constructor(
  name: String,
  aFilter: LoggerFilter? = AlwaysNeutralFilter,
  handler: Handler? = null,
  logLevel: LogLevel? = null
) : Bridge {
  private var filter: LoggerFilter = aFilter ?: AlwaysNeutralFilter
  @Volatile var parent: JdkBridge? = null  // root bridge will have a null parent
  private val jdkLogger: java.util.logging.Logger = java.util.logging.Logger.getLogger(name).apply {
    if (handler != null) addHandler(handler)
    if (logLevel != null) level = logLevel.jdkLevel
  }

  override var logLevel: LogLevel
    get() = LogLevel.fromLevel(jdkLogger.level)
    set(logLevel) {
      jdkLogger.level = logLevel.jdkLevel
    }

  override var includeLocation: Boolean = false

  override val name: String
    get() = jdkLogger.name

  val logToParent: Boolean
    get() = jdkLogger.useParentHandlers

  fun getFilter(): LoggerFilter? {
    return filter
  }

  fun setFilter(filter: LoggerFilter?) {
    this.filter = filter ?: AlwaysNeutralFilter
  }

  override fun shouldIncludeLocation(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return includeLocation || parent?.shouldIncludeLocation(level, marker, throwable) == true
  }

  override fun shouldLogToParent(jdkLogger: com.ealva.ealvalog.Logger): Boolean {
    return !bridgeIsLoggerPeer(jdkLogger) || this.jdkLogger.useParentHandlers
  }

  override fun setLogToParent(logToParent: Boolean) {
    jdkLogger.useParentHandlers = logToParent
  }

  override fun isLoggable(logger: com.ealva.ealvalog.Logger, level: LogLevel): FilterResult {
    return isLoggable(logger, level, NullMarker, NullThrowable)
  }

  /**
   * {@inheritDoc}
   *
   *
   * If the level check passes and any contained filter does not deny, then accepted
   */
  override fun isLoggable(
    logger: com.ealva.ealvalog.Logger,
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    if (!jdkLogger.isLoggable(logLevel.jdkLevel)) {
      return DENY
    }
    val filterResult = filter.isLoggable(logger, logLevel, marker, throwable)
    return if (filterResult === DENY) {
      DENY
    } else NEUTRAL
  }

  override fun log(record: LogRecord) {
    jdkLogger.log(record)
  }

  override fun getLevelForLogger(logger: com.ealva.ealvalog.Logger): LogLevel? {
    return if (bridgeIsLoggerPeer(logger)) {
      LogLevel.fromLevel(jdkLogger.level)
    } else null
  }

  override fun bridgeIsLoggerPeer(logger: com.ealva.ealvalog.Logger): Boolean {
    return name == logger.name
  }

  fun addLoggerHandler(loggerHandler: Handler) {
    jdkLogger.addHandler(loggerHandler)
  }

  fun setToDefault() {
    jdkLogger.useParentHandlers = true
  }
}
