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

package com.ealva.ealvalog

import java.util.logging.LogRecord

/**
 * A no-op [com.ealva.ealvalog.Logger] implementation
 *
 *
 * Created by Eric A. Snell on 2/28/17.
 */
object NullLogger : Logger {
  override val name = "NullLogger"
  override var marker: Marker? = null
  override var logLevel: LogLevel? = null
  override val effectiveLogLevel: LogLevel = LogLevel.NONE
  override var includeLocation: Boolean = false
  override fun isLoggable(level: LogLevel) = false
  override fun isLoggable(level: LogLevel, marker: Marker?, throwable: Throwable?) = false
  override fun log(level: LogLevel, msg: String) {}
  override fun log(level: LogLevel, marker: Marker, msg: String) {}
  override fun log(level: LogLevel, throwable: Throwable, msg: String) {}
  override fun log(level: LogLevel, marker: Marker, throwable: Throwable, msg: String) {}
  override fun log(level: LogLevel, marker: Marker, format: String, vararg formatArgs: Any) {}
  override fun log(level: LogLevel, throwable: Throwable, format: String, vararg formatArgs: Any) {}
  override fun log(
    level: LogLevel,
    marker: Marker,
    throwable: Throwable,
    format: String,
    vararg formatArgs: Any
  ) {}
  override fun log(level: LogLevel, format: String, arg1: Any) {}
  override fun log(level: LogLevel, format: String, arg1: Any, arg2: Any) {}
  override fun log(level: LogLevel, format: String, arg1: Any, arg2: Any, arg3: Any) {}
  override fun log(level: LogLevel, format: String, arg1: Any, arg2: Any, arg3: Any, arg4: Any) {}
  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any,
    arg3: Any,
    arg4: Any,
    vararg remaining: Any
  ) {}
  override fun caught(level: LogLevel, throwable: Throwable) {}
  override fun throwing(level: LogLevel, throwable: Throwable): Throwable = throwable
  override fun logImmediate(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  ) {}
  override fun logImmediate(
    level: LogLevel,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  ) {}
  override fun logImmediate(record: LogRecord) {}
}
