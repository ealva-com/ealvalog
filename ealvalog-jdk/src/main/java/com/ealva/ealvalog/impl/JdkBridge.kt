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
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.NullMarker
import com.ealva.ealvalog.core.Bridge
import com.ealva.ealvalog.core.CoreLogger
import com.ealva.ealvalog.ExtLogRecord
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import com.ealva.ealvalog.util.LogUtil
import com.ealva.ealvalog.util.NullThrowable

import com.ealva.ealvalog.FilterResult.DENY
import com.ealva.ealvalog.FilterResult.NEUTRAL

import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * Bridge the [CoreLogger] to the underlying java.util.logging.Logger
 *
 *
 * Created by Eric A. Snell on 3/7/17.
 */
class JdkBridge internal constructor(name: String) : Bridge {
  @Volatile var parent: JdkBridge? = null  // root bridge will have a null parent
  private val jdkLogger: java.util.logging.Logger
  @Volatile private var filter: LoggerFilter
  /** @return the include location flag
   */
  /** Set the include location flag  */
  override var includeLocation: Boolean = false

  override val name: String
    get() = jdkLogger.name

  override var logLevel: LogLevel
    get() = LogLevel.fromLevel(jdkLogger.level)
    set(logLevel) {
      jdkLogger.level = logLevel.jdkLevel
    }

  val logToParent: Boolean
    get() = jdkLogger.useParentHandlers

  init {
    parent = null
    jdkLogger = Logger.getLogger(name)
    filter = AlwaysNeutralFilter
    includeLocation = false
  }

  constructor(
    loggerName: String,
    filter: LoggerFilter?,
    handler: BaseLoggerHandler?,
    level: LogLevel?
  ) : this(loggerName) {
    setFilter(filter)
    if (handler != null) {
      addLoggerHandler(handler)
    }
    if (level != null) {
      logLevel = level
    }
  }

  fun getFilter(): LoggerFilter? {
    return filter
  }

  fun setFilter(filter: LoggerFilter?) {
    this.filter = filter ?: AlwaysNeutralFilter
  }

  private fun shouldIncludeLocation(): Boolean {
    return includeLocation || parent != null && parent!!.shouldIncludeLocation()
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
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    if (!jdkLogger.isLoggable(level.jdkLevel)) {
      return DENY
    }
    val filterResult = filter.isLoggable(logger, level, marker, throwable)
    return if (filterResult === DENY) {
      DENY
    } else NEUTRAL
  }

  override fun log(
    logger: com.ealva.ealvalog.Logger,
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  ) {
    // ENSURE the record obtained is released!
    //
    // We're not using try with resources here due to warnings about early Android versions.
    val logRecord = ExtLogRecord[level, msg, logger.name, if (shouldIncludeLocation())
      LogUtil.getCallerLocation(stackDepth + 1)
    else
      null, marker, throwable, formatArgs]
    try {
      log(logRecord)
    } finally {
      ExtLogRecord.release(logRecord)
    }
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

  fun addLoggerHandler(loggerHandler: BaseLoggerHandler) {
    jdkLogger.addHandler(loggerHandler)
  }

  fun setToDefault() {
    jdkLogger.useParentHandlers = true
  }
}
