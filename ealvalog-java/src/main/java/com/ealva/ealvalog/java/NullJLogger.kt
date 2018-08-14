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

package com.ealva.ealvalog.java

import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.util.NullThrowable
import java.util.logging.LogRecord

/**
 * Created by Eric A. Snell on 8/10/18.
 */
object NullJLogger : JLogger {
  override fun isLoggable(level: LogLevel) = false
  override fun isLoggable(level: LogLevel, marker: Marker): Boolean = false
  override fun isLoggable(level: LogLevel, throwable: Throwable): Boolean = false
  override fun log(level: LogLevel, msg: String) {}
  override fun log(level: LogLevel, throwable: Throwable, msg: String) {}
  override fun log(level: LogLevel, marker: Marker, format: String, vararg formatArgs: Any) {}
  override fun caught(level: LogLevel, throwable: Throwable) {}
  override val effectiveLogLevel = LogLevel.NONE
  override var includeLocation = false
  override fun resolveLocation(logLevel: LogLevel, marker: Marker?, throwable: Throwable?) = false
  override fun isLoggable(level: LogLevel, marker: Marker?, throwable: Throwable?) = false
  override fun log(level: LogLevel, marker: Marker, msg: String) {}
  override fun log(level: LogLevel, marker: Marker, throwable: Throwable, msg: String) {}
  override fun log(level: LogLevel, throwable: Throwable, format: String, vararg formatArgs: Any) {}
  override fun log(
    level: LogLevel,
    marker: Marker,
    throwable: Throwable,
    format: String,
    vararg formatArgs: Any
  ) {
  }

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
  ) {
  }
  override fun <T : Throwable> throwing(level: LogLevel, throwable: T): T {
    TODO()
  }


  override val name = "NullJLogger"
  override var marker: Marker? = null
  override var logLevel: LogLevel? = LogLevel.NONE
  override fun logImmediate(record: LogRecord) {}
}