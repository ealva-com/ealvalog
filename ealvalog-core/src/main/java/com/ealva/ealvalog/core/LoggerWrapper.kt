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

package com.ealva.ealvalog.core

import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.Marker

import com.ealva.ealvalog.util.LogUtil.combineArgs

import java.util.logging.LogRecord

/**
 * This class is provided as a convenience base class to customize the logger interface. Subclasses can provide a very specific interface of
 * logging taking combinations of primitives and objects, without incurring object creation overhead when logging does not occur.
 *
 *
 * Our experience with production systems, online and batch, has shown us many examples of developers not logging as appropriate or
 * introducing conditional logic because the code path was time critical and trace/debug/info level logging was causing performance problems
 * when not being used. This is especially true in resource constrained environments, such as Android.
 *
 *
 * If the client has numerous low level log statements, eg. [LogLevel.TRACE], [LogLevel.DEBUG], [LogLevel.INFO], etc, and
 * many primitives are logged, this may cause a lot of unnecessary object creation due to autoboxing. If the log level is high, such as
 * [LogLevel.ERROR], every call to log at lower levels will require object creation, though the logging may actually never occur. This
 * class, and it's subclasses (developed by you), gives you the trade-off between an extra method invocation or 'n' number of autoboxing
 * objects created. An small example class is provided.
 *
 *
 * While completely unnecessary to use eAlvaLog, it's expected a client might implement some small number of project specific logging
 * methods and possibly only for areas measured as time critical. Given that most of the work is done via this well tested framework, there
 * is little downside and can be a huge upside to some small custom implementations.
 *
 *
 * In the next example, 2 object creations would be saved if no logging actually occurred when this method was invoked.
 *
 *  <pre>
 * `<p>
 * public void log(final Level level, final String format, final long arg1, final long arg2) {
 * if (realLogger.isLoggable(level)) {
 * realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2);
 * }
 * }
` *
</pre> *
 *
 *
 *
 *
 * In the next example with 5 arguments, 6 object creations would be saved if the logging did not occur: 5 auto boxing and an Object[]
 * allocation. This is compared to the general framework call [.log]
 *
 *
 *
 * <pre>
 * `<p>
 * public void log(final Level level,
 * final String format,
 * final long arg1,
 * final double arg2,
 * final boolean arg3,
 * final long arg4,
 * final double arg5) {
 * if (realLogger.isLoggable(level)) {
 * realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2, arg3, arg4, arg5);
 * }
 * }
` *
</pre> *
 *
 *
 * Created by Eric A. Snell on 3/3/17.
 */
abstract class LoggerWrapper(@Suppress("MemberVisibilityCanBePrivate") protected val realLogger: Logger) : Logger {

  override val name: String
    get() = realLogger.name

  override var marker: Marker?
    get() = realLogger.marker
    set(marker) {
      realLogger.marker = marker
    }

  override var logLevel: LogLevel?
    get() = realLogger.logLevel
    set(logLevel) {
      realLogger.logLevel = logLevel
    }

  override val effectiveLogLevel: LogLevel
    get() = realLogger.effectiveLogLevel

  override var includeLocation: Boolean
    get() = realLogger.includeLocation
    set(includeLocation) {
      realLogger.includeLocation = includeLocation
    }

  override fun isLoggable(level: LogLevel): Boolean {
    return realLogger.isLoggable(level)
  }

  override fun log(level: LogLevel, msg: String) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, msg)
    }
  }

  override fun log(level: LogLevel, marker: Marker, msg: String) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, marker, null, STACK_DEPTH, msg)
    }
  }

  override fun log(level: LogLevel, throwable: Throwable, msg: String) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, throwable, STACK_DEPTH, msg)
    }
  }

  override fun log(
    level: LogLevel,
    marker: Marker,
    throwable: Throwable,
    msg: String
  ) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, marker, throwable, STACK_DEPTH, msg)
    }
  }

  override fun log(
    level: LogLevel,
    marker: Marker,
    format: String,
    vararg formatArgs: Any
  ) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, marker, null, STACK_DEPTH, format, *formatArgs)
    }
  }

  override fun log(
    level: LogLevel,
    throwable: Throwable,
    format: String,
    vararg formatArgs: Any
  ) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, throwable, STACK_DEPTH, format, *formatArgs)
    }
  }

  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any
  ) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1)
    }
  }

  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any
  ) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2)
    }
  }

  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any,
    arg3: Any
  ) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2, arg3)
    }
  }

  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any,
    arg3: Any,
    arg4: Any
  ) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2, arg3, arg4)
    }
  }

  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any,
    arg3: Any,
    arg4: Any,
    vararg remaining: Any
  ) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(
        level,
        null,
        STACK_DEPTH,
        format,
        *combineArgs(remaining, arg1, arg2, arg3, arg4)
      )
    }
  }

  override fun logImmediate(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  ) {
    realLogger.logImmediate(level, marker, throwable, stackDepth, msg, *formatArgs)
  }

  override fun logImmediate(record: LogRecord) {
    realLogger.logImmediate(record)
  }

  override fun isLoggable(level: LogLevel, marker: Marker?, throwable: Throwable?): Boolean {
    return realLogger.isLoggable(level, marker, throwable)
  }

  override fun caught(level: LogLevel, throwable: Throwable) {
    realLogger.caught(level, throwable)
  }

  override fun throwing(level: LogLevel, throwable: Throwable): Throwable {
    return realLogger.throwing(level, throwable)
  }

  companion object {
    const val STACK_DEPTH = 2
  }
}
