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

package com.ealva.ealvalog.impl

import com.ealva.ealvalog.FilterResult
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import java.util.logging.Handler

/**
 * Base class implementation of a [Handler]
 *
 * Created by Eric A. Snell on 3/13/17.
 */
abstract class BaseLoggerHandler(var loggerFilter: LoggerFilter = AlwaysNeutralFilter) : Handler(),
  LoggerFilter {

  override fun shouldIncludeLocation(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return loggerFilter.shouldIncludeLocation(level, marker, throwable)
  }

  override fun isLoggable(logger: Logger, level: LogLevel): FilterResult {
    return isLoggable(logger, level, null, null)
  }
}
