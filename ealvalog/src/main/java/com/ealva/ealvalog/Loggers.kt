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

package com.ealva.ealvalog

import com.ealva.ealvalog.util.LogUtil

import kotlin.reflect.KClass

/**
 * Created by Eric A. Snell on 8/8/18.
 */
fun logger(name: String, marker: Marker? = null, includeLocation: Boolean = false): Logger {
  return Loggers.get(name, marker, includeLocation)
}

fun <T : Any> logger(
  forClass: Class<T>,
  marker: Marker? = null,
  includeLocation: Boolean = false
): Logger {
  return logger(forClass.name, marker, includeLocation)
}

fun <T : Any> logger(
  forClass: KClass<T>,
  marker: Marker? = null,
  includeLocation: Boolean = false
): Logger {
  return logger(forClass.java, marker, includeLocation)
}

fun <T : Any> lazyLogger(
  forClass: KClass<T>,
  marker: Marker? = null,
  includeLocation: Boolean = false
): Lazy<Logger> {
  return lazy { logger(forClass.java, marker, includeLocation) }
}

fun <T : Any> T.logger(marker: Marker? = null, includeLocation: Boolean = false): Logger {
  return logger(javaClass, marker, includeLocation)
}

fun <T : Any> T.lazyLogger(marker: Marker? = null, includeLocation: Boolean = false): Lazy<Logger> {
  return lazy { logger(javaClass, marker, includeLocation) }
}

inline fun Logger.t(
  throwable: Throwable? = null,
  marker: Marker? = null,
  block: (LogEntry) -> Unit
) {
  if (isLoggable(LogLevel.TRACE, marker, throwable)) {
    ExtLogRecord.get(LogLevel.TRACE, name, marker, throwable).use { record ->
      block(record)
      logImmediate(record)
    }
  }
}

inline fun Logger.d(
  throwable: Throwable? = null,
  marker: Marker? = null,
  block: (LogEntry) -> Unit
) {
  if (isLoggable(LogLevel.DEBUG, marker, throwable)) {
    ExtLogRecord.get(LogLevel.DEBUG, name, marker, throwable).use { record ->
      block(record)
      logImmediate(record)
    }
  }
}

inline fun Logger.i(
  throwable: Throwable? = null,
  marker: Marker? = null,
  block: (LogEntry) -> Unit
) {
  if (isLoggable(LogLevel.INFO, marker, throwable)) {
    ExtLogRecord.get(LogLevel.INFO, name, marker, throwable).use { record ->
      block(record)
      logImmediate(record)
    }
  }
}

inline fun Logger.w(
  throwable: Throwable? = null,
  marker: Marker? = null,
  block: (LogEntry) -> Unit
) {
  if (isLoggable(LogLevel.WARN, marker, throwable)) {
    ExtLogRecord.get(LogLevel.WARN, name, marker, throwable).use { record ->
      block(record)
      logImmediate(record)
    }
  }
}

inline fun Logger.e(
  throwable: Throwable? = null,
  marker: Marker? = null,
  block: (LogEntry) -> Unit
) {
  if (isLoggable(LogLevel.ERROR, marker, throwable)) {
    ExtLogRecord.get(LogLevel.ERROR, name, marker, throwable).use { record ->
      block(record)
      logImmediate(record)
    }
  }
}

inline fun Logger.wtf(
  throwable: Throwable? = null,
  marker: Marker? = null,
  block: (LogEntry) -> Unit
) {
  if (isLoggable(LogLevel.CRITICAL, marker, throwable)) {
    ExtLogRecord.get(LogLevel.CRITICAL, name, marker, throwable).use { record ->
      block(record)
      logImmediate(record)
    }
  }
}

/**
 * This is where [Logger] instances are obtained. This singleton must be configured with a concrete
 * implementation of [LoggerFactory] before use. Setting this factory is the
 * responsibility of the client. It's expected this will be done during application initialization.
 *
 * &nbsp;
 *
 * Canonical use:
 *
 * ```kotlin
 * private val LOG = logger(MyClass::class)
 * class MyClass {
 *   fun someMethod(arg1: String, arg2: Int) {
 *      LOG.w { it("%s %d", arg1, arg2) }
 *   }
 * }
 * ```
 * &nbsp;
 *
 * or for a lazy version:
 * ```kotlin
 * private val LOG by lazyLogger(MyClass::class)
 * class MyClass {
 *   fun someMethod(name: String, number: Int) {
 *      LOG.i { +it("name=%s number=%d", name, number) }
 *   }
 * }
 * ```
 *
 * Created by Eric A. Snell on 2/28/17.
 */
object Loggers {
  private const val STACK_DEPTH = 1
  @Volatile private var loggerFactory: LoggerFactory = NullLoggerFactory

  /**
   * Get the root logger. Logger with name [LoggerFactory.ROOT_LOGGER_NAME]
   *
   * @return the root logger
   */
  val root: Logger
    get() = loggerFactory.get(LoggerFactory.ROOT_LOGGER_NAME)

  /** Set the [LoggerFactory] to be used for all calls to obtain a Logger  */
  fun setFactory(factory: LoggerFactory) {
    loggerFactory = factory
  }

  fun get(name: String, marker: Marker? = null, includeLocation: Boolean = false): Logger {
    return loggerFactory.get(name, marker, includeLocation)
  }

  /**
   * Convenience method to obtain a [Logger] for the current object's class.
   *
   * @return a logger for for the caller's class
   */
  fun get(marker: Marker? = null, includeLocation: Boolean = false, stackDepth: Int = 1): Logger {
    return loggerFactory.get(
      LogUtil.getCallerClassName(STACK_DEPTH + stackDepth),
      marker,
      includeLocation
    )
  }
}
