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

import com.ealva.ealvalog.filter.AlwaysDenyFilter

/**
 * A no-op [com.ealva.ealvalog.Logger] implementation
 *
 * Created by Eric A. Snell on 2/28/17.
 */
object NullLogger : Logger {
  override var filter: LoggerFilter = AlwaysDenyFilter
  override val name = "NullLogger"
  override var marker: Marker? = null
  override var logLevel: LogLevel? = null
  override val effectiveLogLevel: LogLevel = LogLevel.NONE
  override var includeLocation: Boolean = false
  override fun shouldIncludeLocation(logLevel: LogLevel, marker: Marker?, throwable: Throwable?) =
    false
  override fun isLoggable(level: LogLevel, marker: Marker?, throwable: Throwable?) = false
  override fun getLogEntry(
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    mdc: Map<String, String>?,
    ndc: List<String>?
  ) = NullLogEntry
  override fun logImmediate(entry: LogEntry) {}
}
