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

import java.io.ObjectStreamException
import java.util.Locale

@Suppress("unused")
object NullLogEntry : LogEntry {
  override fun close() {}
  override val logLevel = LogLevel.NONE
  override val threadName = ""
  override var marker: Marker? = null
  override val location: StackTraceElement? = null
  override val sequenceNumber = 0L
  override val sourceClassName = ""
  override val sourceMethodName = ""
  override val message = ""
  override val threadID = 0
  override val millis = 0L
  override val thrown: Throwable? = null
  override val loggerName = ""
  override val threadPriority = 0
  override val nanoTime = 0L
  override val loggerFQCN: String = NullLogger.javaClass.name
  override val mdc: Map<String, String> = emptyMap()
  override val ndc: List<String> = emptyList()
  override fun reset() = this
  override fun append(c: Char) = this
  override fun append(csq: CharSequence?) = this
  override fun append(str: String) = this
  override fun append(csq: CharSequence?, start: Int, end: Int) = this
  override fun append(b: Boolean) = this
  override fun append(i: Int) = this
  override fun append(lng: Long) = this
  override fun append(f: Float) = this
  override fun append(d: Double) = this
  override fun format(format: String, vararg args: Any) = this
  override fun format(locale: Locale, format: String, vararg args: Any) = this
  override fun log(format: String, vararg args: Any) = this
  override fun addLocation(stackDepth: Int) = this
  @Throws(ObjectStreamException::class)
  private fun readResolve(): Any = NullLogEntry
}
