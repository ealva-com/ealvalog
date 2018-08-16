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

@file:Suppress("unused")

package com.ealva.ealvalog.filter

import com.ealva.ealvalog.FilterResult
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.NullMarker
import com.ealva.ealvalog.util.NullThrowable

import com.ealva.ealvalog.FilterResult.DENY
import com.ealva.ealvalog.FilterResult.NEUTRAL

/**
 * Filter which checks Marker equality
 *
 * Created by Eric A. Snell on 3/13/17.
 */
class MarkerFilter(
  private val marker: Marker,
  whenMatched: FilterResult = NEUTRAL,
  whenDiffer: FilterResult = DENY,
  includeLocation: Boolean = false
) : BaseFilter(whenMatched, whenDiffer, includeLocation) {

  /** For Java clients */
  class Builder(val marker: Marker) {
    private var whenMatched = NEUTRAL
    private var whenDiffer = DENY

    fun whenMatched(whenMatched: FilterResult): Builder {
      this.whenMatched = whenMatched
      return this
    }

    fun whenDiffer(whenDiffer: FilterResult): Builder {
      this.whenDiffer = whenDiffer
      return this
    }

    /**
     * @throws IllegalStateException if Marker has not been set
     */
    @Throws(IllegalStateException::class)
    fun build(): MarkerFilter {
      return MarkerFilter(marker, whenMatched, whenDiffer)
    }

  }

  override fun isLoggable(logger: Logger, level: LogLevel): FilterResult {
    return isLoggable(logger, level, NullMarker, NullThrowable)
  }

  override fun isLoggable(
    logger: Logger,
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    return result(if (marker != null) this.marker.isOrContains(marker) else false)
  }

  companion object {
    /** For Java clients */
    fun builder(marker: Marker): Builder {
      return Builder(marker)
    }
  }

}
