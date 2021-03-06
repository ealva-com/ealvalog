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
import com.ealva.ealvalog.Marker

/**
 * Implementation that logs everything and requests location information be provided
 *
 *
 * Created by Eric A. Snell on 3/3/17.
 */
class DebugLogHandler : BaseLogHandler() {
  override fun isLoggable(
    tag: String,
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return logLevel != LogLevel.NONE
  }

  override fun shouldIncludeLocation(
    tag: String,
    androidLevel: Int,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return true
  }

}
