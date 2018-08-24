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

package com.ealva.ealvalog.jdka

import android.annotation.SuppressLint
import android.util.Log
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.core.ExtRecordFormatter
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import com.ealva.ealvalog.jul.JdkFilter
import com.ealva.ealvalog.util.LogUtil.tagFromName
import java.util.logging.ErrorManager
import java.util.logging.Formatter
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord

val LogRecord.tag: String
  inline get() = tagFromName(loggerName)

/**
 * Convert from an expected Jdk Level to the Android log level. So we don't have to do to
 * map to LogLevel and back Android level. Unexpected maps to ERROR.
 */
private fun Level.toAndroid(): Int {
  return when (this) {
    LogLevel.ALL.jdkLevel -> Log.VERBOSE
    LogLevel.TRACE.jdkLevel -> Log.VERBOSE
    LogLevel.DEBUG.jdkLevel -> Log.DEBUG
    LogLevel.INFO.jdkLevel -> Log.INFO
    LogLevel.WARN.jdkLevel -> Log.WARN
    LogLevel.ERROR.jdkLevel -> Log.ERROR
    LogLevel.CRITICAL.jdkLevel -> Log.ASSERT
    LogLevel.NONE.jdkLevel -> Int.MAX_VALUE
    else -> Log.ERROR // unexpected error level in the log should flag this issue
  }
}

/**
 * Handler for the jdk facade implementation which logs to the Android Log
 *
 *
 * Created by Eric A. Snell on 3/10/17.
 */
open class AndroidLoggerHandler(
  aFormatter: Formatter,
  loggerFilter: LoggerFilter,
  anErrorMgr: ErrorManager
) : Handler() {

  init {
    formatter = aFormatter
    filter = JdkFilter(loggerFilter)
    errorManager = anErrorMgr
  }

  @SuppressLint("LogTagMismatch")
  override fun publish(record: LogRecord?) {
    record?.let { logRecord ->
      if (isLoggable(logRecord)) {
        val tag = logRecord.tag
        val msg: String = formatter.format(logRecord)
        log(logRecord, tag, msg)
      }
    }
  }

  protected open fun log(record: LogRecord, tag: String, msg: String) {
    val androidLevel = record.level.toAndroid()
    when (androidLevel) {
      Log.VERBOSE -> Log.v(tag, msg, record.thrown)
      Log.DEBUG -> Log.d(tag, msg, record.thrown)
      Log.INFO -> Log.i(tag, msg, record.thrown)
      Log.WARN -> Log.w(tag, msg, record.thrown)
      Log.ERROR -> Log.e(tag, msg, record.thrown)
      Log.ASSERT -> Log.wtf(tag, msg, record.thrown)
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
