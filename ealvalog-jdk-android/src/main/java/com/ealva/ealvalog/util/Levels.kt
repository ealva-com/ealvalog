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

package com.ealva.ealvalog.util

import com.ealva.ealvalog.LogLevel

import android.util.Log

/**
 * Utility methods
 *
 * Created by Eric A. Snell on 3/14/17.
 */
object Levels {
  // same as in ealvalog-android, but we don't want a dependency or to factor out a lib with only this
  fun toAndroidLevel(level: LogLevel): Int {
    return when (level) {
      LogLevel.TRACE -> Log.VERBOSE
      LogLevel.DEBUG -> Log.DEBUG
      LogLevel.INFO -> Log.INFO
      LogLevel.WARN -> Log.WARN
      LogLevel.ERROR -> Log.ERROR
      LogLevel.CRITICAL -> Log.ASSERT
      else -> throw IllegalArgumentException("Illegal Level to map to Android")
    }
  }
}
