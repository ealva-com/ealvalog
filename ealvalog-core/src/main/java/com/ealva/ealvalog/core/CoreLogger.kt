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

import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.logLevel
import com.ealva.ealvalog.marker
import java.util.logging.LogRecord

/**
 * This logger delegates to a [Bridge] for [.isLoggable] and
 * [.printLog]
 *
 *
 * Created by Eric A. Snell on 3/7/17.
 */
abstract class CoreLogger<T : Bridge> protected constructor(
  @field:Volatile protected open var bridge: T
) : Logger {

  abstract var logToParent: Boolean

  abstract fun shouldLogToParent(): Boolean

  override fun log(record: LogRecord) {
    if (isLoggable(record.logLevel, record.marker, record.thrown)) {
      bridge.log(record)
    }
  }

  override fun logImmediate(record: LogRecord) {
    bridge.log(record)
  }

  abstract fun setFilter(filter: LoggerFilter)
}
