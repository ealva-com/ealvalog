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

import com.ealva.ealvalog.util.LogUtil

/**
 * This is where [Logger] instances are obtained. This singleton must be configured with a concrete implementation of [ ] before use.
 *
 *
 * This class has a dependency on a concrete [LoggerFactory] instance which must be set before use. Setting and using this factory is
 * the responsibility of the client. It's expected this will be done during application load.
 *
 *
 * Canonical use is to declare a static Logger obtained from a static get() method.
 *
 *
 * <pre>
 * `class MyClass {
 * private static final Logger logger = Loggers.get();
 * }
 * ` *
 * </pre> *
 *
 *
 * As a convenience, static log() methods are provided on this class for "quick and dirty" logging. The Logger to be used is determined from
 * the call stack, which is an expensive operation. Use sparingly and not in time critical areas.
 *
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
    get() = loggerFactory[LoggerFactory.ROOT_LOGGER_NAME]

  /** Set the [LoggerFactory] to be used for all calls to obtain a Logger  */
  fun setFactory(factory: LoggerFactory) {
    loggerFactory = factory
  }

  /**
   * Convenience method to obtain a [Logger] for the current object's class. The follow are equivalent:
   *
   *
   * <pre>
   * `class MyClass {
   * private static final Logger logger = Loggers.get();
   * }
  ` *
   * class MyClass {
   * private static final Logger logger = Loggers.get(MyClass.class);
   * }
  </pre> *
   *
   *
   *
   * @return a logger for for the caller's class
   */
  fun get(): Logger {
    return loggerFactory[LogUtil.getCallerClassName(STACK_DEPTH)]
  }

  /**
   * Get a [Logger] inferring the name from the call stack
   *
   * @param marker each log statement from the returned logger should include this [Marker] unless another Marker is used in the log()
   * call
   *
   * @return logger for the caller's class
   *
   * @see .get
   */
  operator fun get(marker: Marker): Logger {
    return loggerFactory[LogUtil.getCallerClassName(STACK_DEPTH), marker]
  }

  /**
   * Get a [Logger] for `aClass`, using the class name
   *
   * @param aClass logger name comes from the name of this class
   *
   * @return a logger for `aClass`
   */
  operator fun get(aClass: Class<*>): Logger {
    return loggerFactory[aClass.name]
  }

  /**
   * Get a [Logger] for `aClass`, using the class name
   *
   * @param name name of the logger. It's assumed this name is a Class name in the standard form
   *
   * @return a logger for `name`
   */
  operator fun get(name: String): Logger {
    return loggerFactory[name]
  }

  /**
   * Get a [Logger] for `aClass`, using the class name
   *
   * @param aClass logger name comes from the name of this class
   * @param marker each log statement from the returned logger should include this [Marker] unless another Marker is used in the log()
   * call
   *
   * @return a logger for `aClass`
   */
  operator fun get(aClass: Class<*>, marker: Marker): Logger {
    return loggerFactory[aClass.name, marker]
  }

  /**
   * Get a [Logger] for `aClass`, using the class name
   *
   * @param name   name of the logger. It's assumed this name is a Class name in the standard form
   * @param marker each log statement from the returned logger should include this [Marker] unless another Marker is used in the log()
   * call
   *
   * @return a logger for `name`
   */
  operator fun get(name: String, marker: Marker): Logger {
    return loggerFactory[name, marker]
  }
}
