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

/**
 * This is where [Marker] instances are obtained. This singleton defaults to using
 * [NullMarkerFactory] to create markers. To specialize the type of marker created, a concrete
 * [MarkerFactory] instance which must be set  before use. Setting and using this factory is not
 * thread safe - this is the responsibility of the client. It's expected this will be done during
 * application load.
 *
 * Created by Eric A. Snell on 2/28/17.
 */
object Markers {
  @field:Volatile private var markerFactory: MarkerFactory = NullMarkerFactory

  @Suppress("unused")
  fun setFactory(factory: MarkerFactory?) {
    markerFactory = factory ?: NullMarkerFactory
  }

  operator fun get(name: String): Marker {
    return markerFactory[name]
  }

  @Suppress("unused")
  fun orphan(name: String): Marker {
    return markerFactory.makeOrphan(name)
  }
}
