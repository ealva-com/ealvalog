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
import com.ealva.ealvalog.MarkerFactory

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Factory which creates [Marker] instances
 *
 * Created by Eric A. Snell on 2/28/17.
 */
class MarkerFactoryImpl : MarkerFactory {
  private val nameMarkerMap: ConcurrentMap<String, Marker> = ConcurrentHashMap()

  override fun get(name: String): Marker {
    var marker: Marker? = nameMarkerMap[name]
    if (marker == null) {
      marker = MarkerImpl(name, this)
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
    return com.ealva.ealvalog.core.MarkerImpl(name, this)
  }
}
