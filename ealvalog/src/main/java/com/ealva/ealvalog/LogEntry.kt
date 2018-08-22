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

import java.io.Closeable
import java.util.Formatter
import java.util.Locale

operator fun LogEntry.invoke(format: String): LogEntry {
  append(format)
  return this
}

operator fun LogEntry.invoke(format: String, vararg args: Any): LogEntry {
  append(format, *args)
  return this
}

operator fun LogEntry.unaryPlus() {
  addLocation(1)
}

/**
 * Created by Eric A. Snell on 6/29/18.
 */
@Suppress("unused")
interface LogEntry : Appendable, Closeable {
  // specify close as not throwing
  override fun close()

  val logLevel: LogLevel

  val threadName: String

  var marker: Marker?

  val location: StackTraceElement?

  val sequenceNumber: Long

  val sourceClassName: String

  val sourceMethodName: String

  val message: String

  val threadID: Int

  val millis: Long

  val thrown: Throwable?

  val loggerName: String

  val threadPriority: Int

  val nanoTime: Long

  /**
   * Reset the message, ie. `setLength(0)`
   */
  fun reset(): LogEntry

  fun append(str: String): LogEntry

  fun append(b: Boolean): LogEntry

  override fun append(c: Char): LogEntry

  fun append(i: Int): LogEntry

  fun append(lng: Long): LogEntry

  fun append(f: Float): LogEntry

  fun append(d: Double): LogEntry

  /**
   * Format the `format` string with the given set of `args` into the contained [StringBuilder]
   *
   * @param format the format string as defined in [Formatter]
   * @param args   arguments pass for [Formatter.format]
   *
   * @return self
   */
  fun append(
    format: String,
    vararg args: Any
  ): LogEntry

  /**
   * Format the `format` string with the given set of `args` into the contained [StringBuilder]
   *
   * @param locale the [Locale] to use during formatting
   * @param format the format string as defined in [Formatter]
   * @param args   arguments pass for [Formatter.format]
   *
   * @return self
   */
  fun append(
    locale: Locale,
    format: String,
    vararg args: Any
  ): LogEntry

  /**
   * Add the source location, determined by examining the call stack, to the log record. This is
   * an expensive operation as the JVM has to fill out the entire stack frame.
   *
   * @param stackDepth should typically be 0 to add the current location
   *
   * @return this LogEntry
   */
  fun addLocation(stackDepth: Int): LogEntry

}
