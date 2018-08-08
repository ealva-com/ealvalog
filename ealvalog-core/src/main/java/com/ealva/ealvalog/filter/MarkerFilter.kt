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
class MarkerFilter internal constructor(
  private val marker: Marker,
  whenMatched: FilterResult,
  whenDiffer: FilterResult
) : BaseFilter(whenMatched, whenDiffer) {

  class Builder {
    private var marker: Marker? = null
    private var whenMatched = NEUTRAL
    private var whenDiffer = DENY

    fun marker(marker: Marker?): Builder {
      this.marker = marker
      return this
    }

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
      if (marker == null) {
        throw IllegalStateException("Marker required")
      }
      return MarkerFilter(marker!!, whenMatched, whenDiffer)
    }

  }

  override fun isLoggable(logger: Logger, level: LogLevel): FilterResult {
    return isLoggable(logger, level, NullMarker, NullThrowable)
  }

  override fun isLoggable(
    logger: Logger,
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    return result(if (marker != null) this.marker.isOrContains(marker) else false)
  }

  companion object {

    fun builder(): Builder {
      return Builder()
    }
  }

}
