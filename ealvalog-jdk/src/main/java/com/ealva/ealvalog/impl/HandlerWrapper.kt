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

import com.ealva.ealvalog.ExtLogRecord
import com.ealva.ealvalog.FilterResult
import com.ealva.ealvalog.FilterResult.DENY
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Loggers
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.NullMarker
import com.ealva.ealvalog.util.NullThrowable
import java.util.logging.Handler
import java.util.logging.LogRecord

/**
 * Wrap a JUL handler
 *
 * Created by Eric A. Snell on 3/8/17.
 */
open class HandlerWrapper internal constructor(
  protected val realHandler: Handler,
  private val filter: LoggerFilter
) : BaseLoggerHandler(filter) {

  override fun shouldIncludeLocation(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return filter.shouldIncludeLocation(level, marker, throwable)
  }

  override fun publish(record: LogRecord) {
    val thrown = record.thrown
    if (isLoggable(
        Loggers.get(record.loggerName),
        LogLevel.fromLevel(record.level),
        getRecordMarker(record),
        thrown ?: NullThrowable
      ) !== DENY
    ) {
      realHandler.publish(record)
    }
  }

  override fun isLoggable(
    logger: Logger,
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    return if (shouldNotLogAt(logLevel)) DENY else applyFilter(logger, logLevel, marker, throwable)
  }

  private fun applyFilter(logger: Logger, level: LogLevel, marker: Marker?, throwable: Throwable?) =
    loggerFilter.isLoggable(logger, level, marker, throwable).acceptIfNeutral()

  private fun shouldNotLogAt(level: LogLevel) =
    level.shouldNotLogAtLevel(realHandler.level.intValue())

  private fun getRecordMarker(record: LogRecord): Marker {
    return (record as? ExtLogRecord)?.marker ?: NullMarker
  }

  override fun flush() {
    realHandler.flush()
  }

  @Throws(SecurityException::class)
  override fun close() {
    realHandler.close()
  }

}
