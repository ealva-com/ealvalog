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

package com.ealva.ealvalog

import java.io.Serializable
import java.util.Formattable

/**
 * A Marker is extra data passed to the underlying logging system and it's up to that implementation
 * if/how a Marker is used. Examples might be that the Marker is used to filter logging,
 * the Marker is output along with the log message, or the Marker might be used to route the log
 * message.
 *
 * Created by Eric A. Snell on 2/28/17.
 */
interface Marker : Serializable, Iterable<Marker>, Formattable {
  val name: String

  /**
   * Add [marker] as a reference and return true if the set of contained references did not already
   * include [marker]
   */
  fun add(marker: Marker): Boolean

  /**
   * Remove [marker] if it is contained as a reference and return true if set of contained
   * references included [marker]
   */
  fun remove(marker: Marker): Boolean

  /**
   * @return true if this instance is [marker] or this instance has [marker] as a reference
   */
  fun isOrContains(marker: Marker): Boolean

  /**
   * @return true if this instance is named [markerName] or this instance has a reference named
   * [markerName]
   */
  fun isOrContains(markerName: String): Boolean

  /**
   * @return an iterator over child markers
   */
  override fun iterator(): Iterator<Marker>

  /**
   * Essentially the same as `toString()` except the information is appended to the [StringBuilder] parameter. This is a
   * `toString()` variant that works with inheritance and contained objects that support this pattern.
   *
   * This method's contract is that it accepts a non-null builder, appends information to it, and then returns the same StringBuilder it
   * was passed as a parameter.
   *
   * @param builder the builder to append `toString()` information to
   * @param includeContained if string should include contained Markers
   * @return the `builder` parameter to allow call chaining
   */
  fun toStringBuilder(builder: StringBuilder, includeContained: Boolean): StringBuilder
}
