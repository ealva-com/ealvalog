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

package com.ealva.ealvalog.log4j

import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.MarkerFactory
import org.apache.logging.log4j.MarkerManager
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Formatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created by Eric A. Snell on 9/6/18.
 */
object Log4jMarkerFactory : MarkerFactory {
  private val nameMarkerMap: ConcurrentMap<String, Log4jMarker> = ConcurrentHashMap()

  override fun get(name: String): Marker {
    var marker: Marker? = nameMarkerMap[name]
    if (marker == null) {
      marker = Log4jMarker(MarkerManager.getMarker(name))
      val oldMarker = nameMarkerMap.putIfAbsent(name, marker)
      if (oldMarker != null) {
        marker = oldMarker
      }
    }
    return marker
  }

  override fun exists(name: String): Boolean {
    return nameMarkerMap.containsKey(name)
  }

  override fun orphan(name: String): Boolean {
    return nameMarkerMap.remove(name) != null
  }

  override fun makeOrphan(name: String): Marker {
    return Log4jMarker(MarkerManager.getMarker(name))
  }

  fun asLog4jMarker(marker: Marker?): org.apache.logging.log4j.Marker? {
    return when (marker) {
      null -> null
      is Log4jMarker -> marker.log4jMarker
      else -> MarkerManager.getMarker(marker.name)
    }
  }
}

private class Log4jMarker(@Transient var log4jMarker: org.apache.logging.log4j.Marker) : Marker {
  @field:Transient override var name: String = log4jMarker.name
  private set

  override fun add(marker: Marker): Boolean {
    return if (log4jMarker.parents.find { it.name == marker.name } == null) {
      log4jMarker.addParents(MarkerManager.getMarker(marker.name))
      true
    } else false
  }

  override fun remove(marker: Marker): Boolean {
    return log4jMarker.remove(MarkerManager.getMarker(marker.name))
  }

  override fun isOrContains(marker: Marker): Boolean {
    return log4jMarker.isInstanceOf(marker.name)
  }

  override fun isOrContains(markerName: String): Boolean {
    return log4jMarker.isInstanceOf(markerName)
  }

  override fun iterator(): Iterator<Marker> {
    return object : Iterator<Marker> {
      private val iterator = log4jMarker.parents.iterator()
      override fun hasNext(): Boolean {
        return iterator.hasNext()
      }

      override fun next(): Marker {
        return Log4jMarker(iterator.next())
      }

    }
  }

  override fun toStringBuilder(
    builder: StringBuilder,
    includeContained: Boolean
  ): StringBuilder {
    return builder
  }

  override fun formatTo(
    formatter: Formatter,
    flags: Int,
    width: Int,
    precision: Int
  ) {

  }

  @Throws(IOException::class, ClassNotFoundException::class)
  private fun readObject(inputStream: ObjectInputStream) {
    inputStream.defaultReadObject()
    name = inputStream.readUTF()
    log4jMarker = MarkerManager.getMarker(name)
  }

  @Throws(IOException::class)
  private fun writeObject(out: ObjectOutputStream) {
    out.defaultWriteObject()
    out.writeUTF(name)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Log4jMarker

    if (log4jMarker != other.log4jMarker) return false

    return true
  }

  override fun hashCode(): Int {
    return log4jMarker.hashCode()
  }

  companion object {
    private const val serialVersionUID = -7008668159056042921L
  }
}
