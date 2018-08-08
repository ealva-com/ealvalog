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
import com.ealva.ealvalog.core.BaseLogger
import com.ealva.ealvalog.util.LogMessageFormatter
import com.ealva.ealvalog.util.LogMessageFormatterImpl
import com.ealva.ealvalog.util.LogUtil
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.LogRecord

/**
 * [com.ealva.ealvalog.Logger] implementation for Android
 *
 * Created by Eric A. Snell on 3/3/17.
 */
class AndroidLogger internal constructor(
  name: String,
  marker: Marker?,
  override var includeLocation: Boolean
) : BaseLogger(name, marker) {

  private val tag: String = LogUtil.tagFromName(name)

  override var logLevel: LogLevel? = null

  override val effectiveLogLevel: LogLevel
    get() = LogLevel.NONE

  override fun isLoggable(level: LogLevel, marker: Marker?, throwable: Throwable?): Boolean {
    return logHandler.get().isLoggable(tag, Levels.toAndroidLevel(level))
  }

  override fun logImmediate(record: LogRecord) {
    logHandler.get().prepareLog(record)
  }

  override fun printLog(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?,
    stackDepth: Int,
    msg: String,
    vararg formatArgs: Any
  ) {
    val androidLevel = Levels.toAndroidLevel(level)
    logHandler.get().prepareLog(
      tag,
      androidLevel,
      marker,
      throwable,
      getLogSiteInfo(androidLevel, marker, throwable, stackDepth + 1),
      threadLocalFormatter.get(),
      msg,
      *formatArgs
    )
  }

  private fun getLogSiteInfo(
    androidLevel: Int,
    marker: Marker?,
    throwable: Throwable?,
    stackDepth: Int
  ): StackTraceElement? {
    return if (includeLocation || logHandler.get().shouldIncludeLocation(
        tag,
        androidLevel,
        marker,
        throwable
      )
    ) {
      LogUtil.getCallerLocation(stackDepth + 1)
    } else null
  }

  companion object {
    private val threadLocalFormatter = object : ThreadLocal<LogMessageFormatter>() {
      override fun initialValue(): LogMessageFormatter {
        return LogMessageFormatterImpl()
      }

      override fun get(): LogMessageFormatter {
        val lmf = super.get()
        lmf.reset()
        return lmf
      }
    }

    private val logHandler: AtomicReference<LogHandler> = AtomicReference(NullLogHandler)

    fun setHandler(handler: LogHandler) {
      logHandler.set(handler)
    }

    @Suppress("unused")
    fun removeHandler() {
      logHandler.set(NullLogHandler)
    }
  }
}
