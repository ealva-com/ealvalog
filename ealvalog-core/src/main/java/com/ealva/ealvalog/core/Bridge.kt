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
import com.ealva.ealvalog.LoggerFilter
import java.util.logging.LogRecord

/**
 * Instance bridge the [CoreLogger] to the underlying logging implementation
 *
 *
 * Created by Eric A. Snell on 3/7/17.
 */
interface Bridge : LoggerFilter {

  var includeLocation: Boolean

  /**
   * Get the name of the Bridge implementation (typically same as the logger name)
   *
   * @return Bridge implementation name
   */
  val name: String

  /**
   * Get the level for this Bridge instance. Will be the level of the underlying logging framework
   *
   * @return current LogLevel
   */
  val logLevel: LogLevel

  fun shouldLogToParent(jdkLogger: Logger): Boolean

  fun setLogToParent(logToParent: Boolean)

  fun log(record: LogRecord)

  /**
   * If this bridge is for `logger`, then return any set level. Otherwise, this bridge is for a parent and null will be returned.
   *
   * @param logger logger invoking method
   *
   * @return set level or null if no level set or this is not the bridge for the given logger.
   */
  fun getLevelForLogger(logger: Logger): LogLevel?

  fun bridgeIsLoggerPeer(logger: Logger): Boolean
}
