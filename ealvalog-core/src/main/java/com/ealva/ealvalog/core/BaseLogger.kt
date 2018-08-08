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
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.Marker

/**
 * BaseLogger consolidates various logging methods to one method after checking log level
 *
 *
 * Created by Eric A. Snell on 3/1/17.
 */
abstract class BaseLogger protected constructor(
  override val name: String,
  override var marker: Marker? = null
) : Logger {

  override fun isLoggable(level: LogLevel): Boolean {
    return isLoggable(level, marker, null)
  }

  override fun log(level: LogLevel, msg: String) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, msg)
    }
  }

  override fun log(level: LogLevel, marker: Marker, msg: String) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, msg)
    }
  }

  override fun log(level: LogLevel, throwable: Throwable, msg: String) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, msg)
    }
  }

  override fun log(
    level: LogLevel,
    marker: Marker,
    throwable: Throwable,
    msg: String
  ) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, msg)
    }
  }

  override fun log(
    level: LogLevel,
    marker: Marker,
    format: String,
    vararg formatArgs: Any
  ) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, *formatArgs)
    }
  }

  override fun log(
    level: LogLevel,
    throwable: Throwable,
    format: String,
    vararg formatArgs: Any
  ) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, format, *formatArgs)
    }
  }

  override fun log(
    level: LogLevel,
    marker: Marker,
    throwable: Throwable,
    format: String,
    vararg formatArgs: Any
  ) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, format, *formatArgs)
    }
  }

  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any
  ) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1)
    }
  }

  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any
  ) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1, arg2)
    }
  }

  override fun log(
    level: LogLevel,
    format: String,
    arg1: Any,
    arg2: Any,
    arg3: Any
  ) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1, arg2, arg3)
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
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1, arg2, arg3, arg4)
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
    if (isLoggable(level, marker, null)) {
      logImmediate(
        level,
        marker,
        null,
        STACK_DEPTH,
        format,
        arg1, arg2, arg3, arg4, *remaining
      )
    }
  }

  override fun caught(level: LogLevel, throwable: Throwable) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, throwable.localizedMessage)
    }
  }

  override fun throwing(level: LogLevel, throwable: Throwable): Throwable {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, throwable.localizedMessage)
    }
    return throwable
  }

  /**
   * {@inheritDoc}
   *
   *
   * This version passes an optional marker through to the underlying logger. Even if this null, it will override any contained [ ]
   */
  override fun logImmediate(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  ) {
    printLog(level, marker, throwable, stackDepth + 1, msg, *formatArgs)
  }

  /**
   * {@inheritDoc}
   *
   *
   * This version passes down any contained Marker
   */
  override fun logImmediate(
    level: LogLevel,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  ) {
    printLog(level, marker, throwable, stackDepth + 1, msg, *formatArgs)
  }

  /**
   * All logging funnels through here. Subclasses implement this method to perform the actual concrete logging. [.isLoggable] has been checked so this method should proceed as if logging should occur.
   *
   * @param level      the log level
   * @param marker     an optional [Marker]
   * @param throwable  an optional [Throwable]
   * @param stackDepth depth from original log call
   * @param msg        the message or format string passed by the client
   * @param formatArgs any format arguments passed by the client. Never null but may be zero length if no formatting is necessary
   */
  protected abstract fun printLog(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  )

  companion object {
    private const val STACK_DEPTH = 1
  }

}
