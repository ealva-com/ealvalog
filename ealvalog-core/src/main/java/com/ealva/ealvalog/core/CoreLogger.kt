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

import com.ealva.ealvalog.LogEntry
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker

/**
 * This logger delegates to a [Bridge] implementation to perform most operations. The
 * [LoggerConfiguration] is so that the bridge hierarchy can be reconfigured on the fly.
 * Typically a Factory implementation will be the LoggerConfiguration of of the particular
 * Logger type.
 *
 * Created by Eric A. Snell on 3/7/17.
 */
abstract class CoreLogger<T : Bridge> protected constructor(
  loggerName: String,
  private val config: LoggerConfiguration<T>
) : Logger {
  @field:Volatile protected open var bridge: T = config.getBridge(loggerName)

  override var logLevel: LogLevel?
    get() = bridge.getLevelForLogger(this)
    set(logLevel) = config.setLogLevel(this, logLevel ?: LogLevel.NONE)

  override val effectiveLogLevel: LogLevel
    get() = bridge.logLevel

  override var includeLocation: Boolean
    get() = bridge.includeLocation
    set(includeLocation) = config.setIncludeLocation(this, includeLocation)

  override fun shouldIncludeLocation(
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return bridge.shouldIncludeLocation(logLevel, marker, throwable)
  }

  override fun isLoggable(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return bridge.isLoggable(name, level, marker, throwable).shouldProceed
  }

  var logToParent: Boolean
    get() = bridge.logToParent
    set(logToParent) = config.setLogToParent(this, logToParent)

  override var filter: LoggerFilter
    get() = bridge.getFilter()
    set(value) {
      config.setLoggerFilter(this, value)
    }

  fun willLogToParent(): Boolean {
    return bridge.willLogToParent(name)
  }

  override fun logImmediate(entry: LogEntry) {
    bridge.log(entry)
  }

}
