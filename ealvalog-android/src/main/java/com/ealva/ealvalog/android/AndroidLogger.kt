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

package com.ealva.ealvalog.android

import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.logLevel
import com.ealva.ealvalog.marker
import com.ealva.ealvalog.util.LogUtil
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.LogRecord

/**
 * [com.ealva.ealvalog.Logger] implementation for Android
 *
 * Created by Eric A. Snell on 3/3/17.
 */
class AndroidLogger internal constructor(
  override val name: String,
  override var marker: Marker? = null,
  override var includeLocation: Boolean = false
) : Logger {

  private val tag: String = LogUtil.tagFromName(name)

  override var logLevel: LogLevel? = null

  override val effectiveLogLevel: LogLevel
    get() = logLevel ?: LogLevel.NONE

  override fun isLoggable(level: LogLevel, marker: Marker?, throwable: Throwable?): Boolean {
    return logHandler.get().isLoggable(tag, level, marker, throwable)
  }

  override fun shouldIncludeLocation(
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return includeLocation || logHandler.get().shouldIncludeLocation(
      tag,
      logLevel.toAndroid(),
      marker,
      throwable
    )
  }

  override fun log(record: LogRecord) {
    if (isLoggable(record.logLevel, record.marker, record.thrown)) {
      logImmediate(record)
    }
  }

  override fun logImmediate(record: LogRecord) {
    logHandler.get().prepareLog(record)
  }

  companion object {
    private val logHandler: AtomicReference<LogHandler> = AtomicReference(
      NullLogHandler
    )

    fun setHandler(handler: LogHandler) {
      logHandler.set(handler)
    }

    @Suppress("unused")
    fun removeHandler() {
      logHandler.set(NullLogHandler)
    }
  }
}
