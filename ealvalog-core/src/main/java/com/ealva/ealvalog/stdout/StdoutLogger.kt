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

package com.ealva.ealvalog.stdout

import com.ealva.ealvalog.LogEntry
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.MdcContext
import com.ealva.ealvalog.core.ExtLogRecord
import com.ealva.ealvalog.core.ExtRecordFormatter
import com.ealva.ealvalog.filter.AlwaysNeutralFilter

private val loggerFQCN: String = StdoutLogger::class.java.name

class StdoutLogger(
  override val name: String,
  override var marker: Marker? = null,
  override var includeLocation: Boolean = false
) : Logger {
  private val formatter = ExtRecordFormatter(ExtRecordFormatter.TYPICAL_FORMAT)

  override var filter: LoggerFilter = AlwaysNeutralFilter

  override var logLevel: LogLevel? = null

  override val effectiveLogLevel: LogLevel
    get() = logLevel ?: LogLevel.NONE

  override fun isLoggable(level: LogLevel, marker: Marker?, throwable: Throwable?): Boolean {
    return filter.isLoggable(name, level, marker ?: this.marker, throwable).shouldProceed
  }

  override fun shouldIncludeLocation(
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean = includeLocation

  override fun getLogEntry(
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    mdcContext: MdcContext?
  ): LogEntry {
    return ExtLogRecord.get(
      loggerFQCN,
      logLevel,
      name,
      marker ?: this.marker,
      throwable,
      mdcContext?.mdc,
      mdcContext?.ndc
    )
  }

  override fun logImmediate(entry: LogEntry) {
    ExtLogRecord.fromLogEntry(entry).use { record ->
      println(formatter.format(record))
    }
  }
}
