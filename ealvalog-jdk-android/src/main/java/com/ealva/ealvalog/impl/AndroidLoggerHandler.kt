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
import com.ealva.ealvalog.core.ExtRecordFormatter
import com.ealva.ealvalog.filter.AlwaysNeutralFilter

import com.ealva.ealvalog.util.Levels

import com.ealva.ealvalog.FilterResult.DENY
import com.ealva.ealvalog.util.LogUtil.tagFromName

import android.util.Log

import java.util.logging.ErrorManager
import java.util.logging.Formatter
import java.util.logging.Handler
import java.util.logging.LogRecord

/**
 * Handler for the jdk facade implementation which logs to the Android Log
 *
 *
 * Created by Eric A. Snell on 3/10/17.
 */
class AndroidLoggerHandler private constructor(
  formatter: Formatter,
  filter: LoggerFilter,
  errorManager: ErrorManager
) : BaseLoggerHandler(filter) {

  init {
    setFormatter(formatter)
    setErrorManager(errorManager)
  }

  override fun isLoggable(
    logger: Logger,
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    return if (isLoggable(tagFromName(logger.name), Levels.toAndroidLevel(level))) {
      loggerFilter.isLoggable(logger, level, marker, throwable)
    } else DENY
  }

  private fun isLoggable(tag: String, androidLevel: Int): Boolean {
    return Log.isLoggable(tag, androidLevel)
  }

  override fun publish(record: LogRecord) {
    val androidLevel = Levels.toAndroidLevel(LogLevel.fromLevel(record.level))
    val tag = tagFromName(record.loggerName)
    if (isLoggable(tag, androidLevel)) {
      val msg = formatter.format(record)
      when (androidLevel) {
        Log.VERBOSE -> Log.v(tag, msg, record.thrown)
        Log.DEBUG -> Log.d(tag, msg, record.thrown)
        Log.INFO -> Log.i(tag, msg, record.thrown)
        Log.WARN -> Log.w(tag, msg, record.thrown)
        Log.ERROR -> Log.e(tag, msg, record.thrown)
        Log.ASSERT -> Log.wtf(tag, msg, record.thrown)
      }
    }
  }

  override fun flush() {}

  override fun close() {}

  companion object {
    @JvmOverloads
    fun make(
      formatter: Formatter = ExtRecordFormatter(ExtRecordFormatter.TYPICAL_ANDROID_FORMAT, true),
      filter: LoggerFilter = AlwaysNeutralFilter,
      errorManager: ErrorManager = ErrorManager()
    ): AndroidLoggerHandler {
      return AndroidLoggerHandler(formatter, filter, errorManager)
    }
  }

}
