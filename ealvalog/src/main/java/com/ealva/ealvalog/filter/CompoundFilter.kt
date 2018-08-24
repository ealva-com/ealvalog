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

package com.ealva.ealvalog.filter

import com.ealva.ealvalog.FilterResult
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Created by Eric A. Snell on 8/17/18.
 */
class CompoundFilter(vararg filters: LoggerFilter) : LoggerFilter {
  private val filters = CopyOnWriteArraySet<LoggerFilter>().apply { addAll(filters) }

  fun add(filter: LoggerFilter): Boolean {
    return filters.add(filter)
  }

  fun remove(filter: LoggerFilter): Boolean {
    return filters.remove(filter)
  }

  fun clear() {
    filters.clear()
  }

  fun addAll(vararg filters: LoggerFilter) {
    this.filters.addAll(filters)
  }

  override fun isLoggable(
    loggerName: String,
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    filters.forEach {
      val result = it.isLoggable(loggerName, logLevel, marker, throwable)
      if (result !== FilterResult.NEUTRAL) return result
    }
    return FilterResult.NEUTRAL
  }

}
