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
 * Creates and maintains [Marker] instances
 *
 *
 * Created by Eric A. Snell on 2/28/17.
 */
interface MarkerFactory {

  /**
   * Get a Marker instance, creating it if necessary.
   *
   * @param name name of the marker
   *
   * @return a marker instance contained in this factory
   */
  operator fun get(name: String): Marker

  /**
   * Determine if the Marker has already been created
   *
   * @param name name of the marker
   *
   * @return true if a marker with `name` exists in this factory instance
   */
  fun exists(name: String): Boolean

  /**
   * Remove the named [Marker] from the factory
   * @param name marker name to remove or create
   *
   * @return true if the [Marker] was removed, false if it was not contained in this factory
   */
  fun orphan(name: String): Boolean

  /**
   * Create a [Marker] instance but do not keep a reference in this factory
   *
   * @param name name of the marker to be created
   *
   * @return a new marker instance that is not retained by this factory
   */
  fun makeOrphan(name: String): Marker
}
