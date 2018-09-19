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
import java.io.ObjectStreamException

/**
 * Created by Eric A. Snell on 9/18/18.
 */
@Suppress("unused")
object NullReadOnlyStringMap : ReadOnlyStringMap {
  private const val serialVersionUID = -1540910646789297246L
  override fun isEmpty() = true
  override fun <V : Any?> getValue(key: String?) = null
  override fun size() = 0
  override fun containsKey(key: String?) = false
  override fun <V : Any?> forEach(action: BiConsumer<String, in V>?) {}
  override fun <V : Any?, S : Any?> forEach(action: TriConsumer<String, in V, S>?, state: S) {}
  override fun toMap(): MutableMap<String, String> = mutableMapOf()
  @Throws(ObjectStreamException::class)
  private fun readResolve(): Any = NullReadOnlyStringMap
}
