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

package com.ealva.ealvalog.core

import com.ealva.ealvalog.Marker
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.FormattableFlags.ALTERNATE
import java.util.FormattableFlags.LEFT_JUSTIFY
import java.util.FormattableFlags.UPPERCASE
import java.util.Formatter
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Basic Marker implementation
 *
 *
 * Created by Eric A. Snell on 2/28/17.
 */
open class BasicMarker(override var name: String) : Marker {
  private var containedMarkers = CopyOnWriteArraySet<Marker>()

  private// threadLocalStringBuilder.get(); Not thread local builder until we know we're logging markers a lot
  val stringBuilder: StringBuilder
    get() = StringBuilder()

  override fun add(marker: Marker): Boolean {
    return containedMarkers.add(marker)
  }

  override fun remove(marker: Marker): Boolean {
    return containedMarkers.remove(marker)
  }

  override fun isOrContains(marker: Marker): Boolean {
    return this == marker || containedMarkers.contains(marker)
  }

  override fun isOrContains(markerName: String): Boolean {
    return name == markerName || containedMarkers.find { it.name == markerName  } != null
  }

  override fun iterator(): Iterator<Marker> {
    return containedMarkers.iterator()
  }

  override fun toString(): String {
    return if (containedMarkers.isEmpty()) {
      name
    } else toStringBuilder(StringBuilder(), true).toString()
  }

  override fun toStringBuilder(builder: StringBuilder, includeContained: Boolean): StringBuilder {
    // subclasses would typically invoke super.toStringBuilder(builder) first
    if (containedMarkers.isEmpty()) {
      return builder.append(name)
    }

    if (includeContained) {
      builder.append(name).append(OPEN)
      containedMarkers.forEachIndexed { i, marker ->
        if (i != 0) {
          builder.append(SEPARATOR)
        }
        marker.toStringBuilder(builder, true)
      }
      builder.append(CLOSE)
    }

    return builder
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  private fun readObject(inputStream: ObjectInputStream) {
    inputStream.defaultReadObject()
    name = inputStream.readUTF()
    @Suppress("UNCHECKED_CAST")
    containedMarkers = inputStream.readObject() as CopyOnWriteArraySet<Marker>
  }

  @Throws(IOException::class)
  private fun writeObject(out: ObjectOutputStream) {
    out.defaultWriteObject()
    out.writeUTF(name)
    out.writeObject(containedMarkers)
  }

  override fun formatTo(formatter: Formatter, flags: Int, width: Int, precision: Int) {
    val useAlternate = flags and ALTERNATE == ALTERNATE
    val leftJustify = flags and LEFT_JUSTIFY == LEFT_JUSTIFY
    val upperCase = flags and UPPERCASE == UPPERCASE
    val builder = stringBuilder
    toStringBuilder(builder, useAlternate)
    if (precision != -1 && builder.length > precision) {
      builder.setLength(precision - 1)
      builder.append('…')
    }
    if (width != -1 && width > builder.length) {
      builder.ensureCapacity(width)
      val padAmount = width - builder.length
      if (leftJustify) {
        for (i in 0 until padAmount) {
          builder.append(' ')
        }
      } else {
        for (i in 0 until padAmount) {
          // could possibly be more efficient to insert from long string of spaces,
          // but Knuth would not approve
          builder.insert(0, ' ')
        }
      }
    }
    formatter.format(if (upperCase) builder.toString().toUpperCase() else builder.toString())
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as BasicMarker

    if (name != other.name) return false

    return true
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }

  companion object {
    private const val serialVersionUID = 445780917635303838L

    private const val OPEN = '['
    private const val SEPARATOR = ','
    private const val CLOSE = ']'
  }
}
