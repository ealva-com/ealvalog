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

import org.apache.logging.log4j.util.BiConsumer
import org.apache.logging.log4j.util.ReadOnlyStringMap
import org.apache.logging.log4j.util.TriConsumer

/**
 * Created by Eric A. Snell on 9/17/18.
 */
class ReadOnlyStringMapAdapter(var map: Map<String, String>) : ReadOnlyStringMap {

  override fun isEmpty(): Boolean {
    return map.isEmpty()
  }

  override fun <V : Any?> getValue(key: String?): V? {
    @Suppress("UNCHECKED_CAST")
    return map[key] as V?
  }

  override fun size(): Int {
    return map.size
  }

  override fun containsKey(key: String?): Boolean {
    return map.containsKey(key)
  }

  override fun <V : Any?> forEach(action: BiConsumer<String, in V>) {
    for ((key, value) in map) {
      //BiConsumer should be able to handle values of any type V. In our case the values are of type String.
      @Suppress("UNCHECKED_CAST")
      action.accept(key, value as V?)
    }

  }

  override fun <V : Any?, S : Any?> forEach(action: TriConsumer<String, in V, S>, state: S) {
    for ((key, value) in map) {
      //TriConsumer should be able to handle values of any type V. In our case the values are of type String.
      @Suppress("UNCHECKED_CAST")
      action.accept(key, value as V?, state)
    }
  }

  override fun toMap(): MutableMap<String, String> {
    return map.toMutableMap()
  }
}