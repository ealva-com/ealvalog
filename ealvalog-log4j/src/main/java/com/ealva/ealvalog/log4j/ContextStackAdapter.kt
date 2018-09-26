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
import org.apache.logging.log4j.spi.MutableThreadContextStack
import org.apache.logging.log4j.spi.ThreadContextStack

/**
 * Created by Eric A. Snell on 9/17/18.
 */
class ContextStackAdapter(var list: MutableList<String> = mutableListOf()) : ThreadContextStack {

  override fun contains(element: String): Boolean {
    return list.contains(element)
  }

  override fun add(element: String): Boolean {
    return list.add(element)
  }

  override fun addAll(elements: Collection<String>): Boolean {
    return list.addAll(elements)
  }

  override fun clear() {
    list.clear()
  }

  override fun trim(depth: Int) {
    require(depth >= 0) { "Maximum stack depth cannot be negative" }
    list = list.subList(0, Math.min(depth, list.size)).toMutableList()
  }

  override fun push(message: String) {
    list.add(message)
  }

  override fun asList(): MutableList<String> {
    return list
  }

  override fun copy(): ThreadContext.ContextStack {
    return MutableThreadContextStack(list)
  }

  override fun containsAll(elements: Collection<String>): Boolean {
    return list.containsAll(elements)
  }

  override fun isEmpty(): Boolean {
    return list.isEmpty()
  }

  override fun iterator(): MutableIterator<String> {
    return list.iterator()
  }

  override fun peek(): String? {
    return list.lastOrNull()
  }

  override fun remove(element: String): Boolean {
    return list.remove(element)
  }

  override fun removeAll(elements: Collection<String>): Boolean {
    return list.removeAll(elements)
  }

  override fun getDepth(): Int {
    return list.size
  }

  override fun retainAll(elements: Collection<String>): Boolean {
    return list.retainAll(elements)
  }

  override fun getImmutableStackOrNull(): ThreadContext.ContextStack {
    return MutableThreadContextStack(list)
  }

  override fun pop(): String {
    if (list.isEmpty()) throw NoSuchElementException("Stack is empty")
    return list.removeAt(list.lastIndex)
  }

  override val size: Int
    get() = list.size

}