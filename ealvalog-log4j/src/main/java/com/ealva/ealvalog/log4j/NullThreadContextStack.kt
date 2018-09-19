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

import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.spi.ThreadContextStack

/**
 * Created by Eric A. Snell on 9/18/18.
 */
object NullThreadContextStack : ThreadContextStack {
  private val serialVersionUID = -8969328620327666899L
  override fun contains(element: String?) = false
  override fun add(element: String?) = false
  override fun addAll(elements: Collection<String>) = false
  override fun clear() {}
  override fun trim(depth: Int) {}
  override fun push(message: String?) {}
  override fun asList(): MutableList<String> = mutableListOf()
  override fun copy(): ThreadContext.ContextStack = NullThreadContextStack
  override fun containsAll(elements: Collection<String>) = false
  override fun isEmpty() = true
  override fun iterator(): MutableIterator<String> = object : MutableIterator<String> {
    override fun hasNext() = false
    override fun next() = throw NoSuchElementException()
    override fun remove() {}
  }
  override fun peek() = null
  override fun remove(element: String?) = false
  override fun removeAll(elements: Collection<String>) = false
  override fun getDepth() = 0
  override fun retainAll(elements: Collection<String>) = false
  override fun getImmutableStackOrNull() = NullThreadContextStack
  override fun pop() = throw NoSuchElementException()
  override val size = 0
}
