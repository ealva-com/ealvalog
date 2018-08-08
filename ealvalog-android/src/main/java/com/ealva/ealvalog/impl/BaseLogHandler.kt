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

import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.core.ExtRecordFormatter
import com.ealva.ealvalog.util.LogMessageFormatter

import android.os.Build
import android.util.Log

import java.util.Locale
import java.util.logging.LogRecord

/**
 * Base handler prepares a default style message including log call site information
 *
 * Created by Eric A. Snell on 3/3/17.
 */
abstract class BaseLogHandler : LogHandler {
  private val formatter = ExtRecordFormatter(ExtRecordFormatter.TYPICAL_ANDROID_FORMAT)

  private val locale: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      Locale.getDefault(Locale.Category.FORMAT)
    } else {
      Locale.getDefault()
    }

  override fun prepareLog(record: LogRecord) {
    log(
      Levels.toAndroidLevel(LogLevel.fromLevel(record.level)),
      record.loggerName,
      formatter.format(record),
      record.thrown
    )
  }

  override fun prepareLog(
    tag: String,
    level: Int,
    marker: Marker?,
    throwable: Throwable?,
    callerLocation: StackTraceElement?,
    formatter: LogMessageFormatter,
    msg: String,
    vararg formatArgs: Any
  ) {
    if (callerLocation != null) {
      formatter.append('(')
        .append(callerLocation.methodName)
        .append(':')
        .append(callerLocation.lineNumber)
        .append(") ")
    }
    val logMessage = formatter.append(locale, msg, *formatArgs)
      .toString()
    log(level, tag, logMessage, throwable)
  }

  private fun log(
    level: Int,
    tag: String,
    logMessage: String,
    throwable: Throwable?
  ) {
    when (level) {
      Log.VERBOSE -> Log.v(tag, logMessage, throwable)
      Log.DEBUG -> Log.d(tag, logMessage, throwable)
      Log.INFO -> Log.i(tag, logMessage, throwable)
      Log.WARN -> Log.w(tag, logMessage, throwable)
      Log.ERROR -> Log.e(tag, logMessage, throwable)
      Log.ASSERT -> Log.wtf(tag, logMessage, throwable)
      else -> Log.wtf(tag, "LOGGER CONFIG ERROR ($level) $logMessage", throwable)
    }
  }

}
