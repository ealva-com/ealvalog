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

import java.util.Formatter
import java.util.logging.LogRecord

/**
 * It's expected all logging occurs through concrete implementations of this interface which are
 * obtained via [Loggers]
 *
 *
 * Created by Eric A. Snell on 2/28/17.
 */
interface Logger {

  val name: String

  /**
   * The [Marker] to use for all logging from this logger, superseded by any marker passed during
   * a logging call.
   */
  var marker: Marker?

  /**
   * An optional LogLevel. If null the nearest ancestor LogLevel will be used as the level
   * for this logger
   *
   * If null is passed to the root logger, it sets the level to [LogLevel.NONE]
   */
  var logLevel: LogLevel?

  /**
   * The level set for this logger or, if that it has not been set, get the nearest ancestor
   * log level
   */
  val effectiveLogLevel: LogLevel

  /**
   * Set if this logger should include call site location information in the log information. This
   * is a relatively expensive operation and defaults to false.
   *
   *
   * The information passed to lower layers of the logging framework include: caller class,
   * caller method, and caller file line number. This is obtained from the call stack. If calling
   * code is obfuscated the results will also be obfuscated.
   *
   * Also note that the underlying log statement formatter must be configured to include this
   * information.
   */
  var includeLocation: Boolean

  /**
   * Determine if a log call at this [LogLevel] will result in an actual log statement.
   * Typically this is only a level check, unless the Logger instance contains a [Marker]. In
   * that case the contained Marker is also checked to promote fast short-circuiting
   *
   * [level] is one of [LogLevel.TRACE], [LogLevel.DEBUG],
   * [LogLevel.INFO], [LogLevel.WARN], [LogLevel.ERROR], [LogLevel.CRITICAL]
   */
  fun isLoggable(level: LogLevel): Boolean

  /**
   * Determine if a log at this [LogLevel], with the given (optional) [Marker] and (optional)
   * [Throwable], result in an actual log statement
   */
  fun isLoggable(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean

  /**
   * If isLoggable, log at the `msg` at `level`
   *
   * @param level log level to use
   * @param msg   log msg which is unaltered
   */
  fun log(level: LogLevel, msg: String)

  /**
   * If isLoggable, log at the `msg` at `level` using the `marker`
   *
   * @param level  log level to use
   * @param marker marker to include
   * @param msg    log msg which is unaltered
   */
  fun log(level: LogLevel, marker: Marker, msg: String)

  /**
   * If isLoggable, log at the `msg` at `level` with the given `throwable`
   *
   * @param level     log level to use
   * @param throwable throwable to include
   * @param msg       log msg which is unaltered
   */
  fun log(level: LogLevel, throwable: Throwable, msg: String)

  /**
   * If isLoggable, log at the `msg` at `level` using the `marker` and
   * `throwable`
   *
   * @param level     log level to use
   * @param marker    marker to include
   * @param throwable throwable to include
   * @param msg       log msg which is unaltered
   */
  fun log(
    level: LogLevel,
    marker: Marker,
    throwable: Throwable,
    msg: String
  )

  /**
   * If isLoggable, log at the `msg` at `level` using the `marker`
   *
   * @param level      log level to use
   * @param marker     marker to include
   * @param format     a format string in the form required by [Formatter]
   * @param formatArgs arguments passed to [Formatter.format]
   */
  fun log(
    level: LogLevel,
    marker: Marker,
    format: String,
    vararg formatArgs: Any
  )

  /**
   * If isLoggable, log at the `msg` at `level` using the `throwable`
   *
   * @param level      log level to use
   * @param throwable  throwable to include
   * @param format     a format string in the form required by [Formatter]
   * @param formatArgs arguments passed to [Formatter.format]
   */
  fun log(
    level: LogLevel,
    throwable: Throwable,
    format: String,
    vararg formatArgs: Any
  )

  /**
   * If isLoggable, log at the `msg` at `level` using the `marker` and
   * `throwable`
   *
   * @param level      log level to use
   * @param marker     marker to include
   * @param throwable  throwable to include
   * @param format     a format string in the form required by [Formatter]
   * @param formatArgs arguments passed to [Formatter.format]
   */
  fun log(
    level: LogLevel,
    marker: Marker,
    throwable: Throwable,
    format: String,
    vararg formatArgs: Any
  )

  fun log(level: LogLevel, format: String, arg1: Any)

  fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any
  )

  fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any,
    arg3: Any
  )

  fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any,
    arg3: Any,
    arg4: Any
  )

  fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any,
    arg3: Any,
    arg4: Any,
    vararg remaining: Any
  )

  /**
   * Used to log an exception being caught where no message is needed
   *
   * @param level     log level to use
   * @param throwable the throwable that was caught
   */
  fun caught(level: LogLevel, throwable: Throwable)

  /**
   * Log a throwable being thrown at the log site.
   *
   *
   * `throw LOG.throwing(LogLevel.ERROR, new MyException("Important Info"));
  ` *
   *
   * @param level     level at which to log
   * @param throwable the Throwable/Exception/Error to log
   *
   * @return returns the throwable for convenience
   */
  fun throwing(level: LogLevel, throwable: Throwable): Throwable

  /**
   * Log without checking the the level and indicate where on the call chain the log is occurring
   * (`stackDepth`). This method's primary use is for this logging framework and it's not
   * expected client's would typically use this method.
   *
   *
   * This method has a [Marker] parameter which will override any [Marker] in the
   * Logger itself
   *
   * @param level      log level - will not be checked before logging
   * @param marker     an optional [Marker]
   * @param throwable  an optional [Throwable]
   * @param stackDepth the level of indirection from the original "log" invocation. This must be 0
   * if a client invokes this method.
   * @param msg        the log message
   * @param formatArgs any formatting arguments if `msg` is a printf style format string
   * (see [Formatter]
   */
  fun logImmediate(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  )

  /**
   * Log without checking the the level and indicate where on the call chain the log is occurring
   * (`stackDepth`). This method's primary use is for this logging framework and it's not
   * expected client's would typically use this method.
   *
   * @param level      log level - will not be checked before logging
   * @param throwable  an optional [Throwable]
   * @param stackDepth the level of indirection from the original "log" invocation. This must be 0
   * if a client invokes this method.
   * @param msg        the log message
   * @param formatArgs any formatting arguments if `msg` is a printf style format string
   * (see [Formatter]
   */
  fun logImmediate(
    level: LogLevel,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  )

  /**
   * Log without checking the the level. This method's primary use is for this logging framework
   * and it's not expected client's would typically use this method.
   *
   * @param record the full log information
   */
  fun logImmediate(record: LogRecord)
}

