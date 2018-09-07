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
import java.util.Locale

/**
 * Appends the [msg] to the the [LogEntry]
 */
operator fun LogEntry.invoke(msg: String): LogEntry {
  append(msg)
  return this
}

/**
 * Add the [format] string and [args] to the [LogEntry] so the information is available to
 * the underlying logging framework. The [format] string style depends on the logging framework,
 * but is typically similar to the style required by [java.text.MessageFormat]. This framework
 * prefers the printf style of [String.format], which uses [java.util.Formatter], though there is
 * is a small performance penalty for String.format() over java.util.Formatter
 *
 * See [LogEntry.log]
 */
operator fun LogEntry.invoke(format: String, vararg args: Any): LogEntry {
  log(format, *args)
  return this
}

/**
 * Add the source location, determined by examining the call stack, to the log record. This is
 * an expensive operation as the JVM has to fill out the entire stack frame.
 *
 * See [LogEntry.addLocation]
 */
operator fun LogEntry.unaryPlus() {
  addLocation(1)
}

/**
 * A LogEntry is obtained from a [Logger] and logging information is added to it before it is sent
 * back to the [Logger]. This is typically via extension functions in Kotlin or the JLogger
 * interface in Java code.
 *
 * - Logger use (canonical)
 * ```java
 * public class MainActivity extends Activity  {
 *   private static final JLogger LOG = JLoggers.get(MainActivity.class);
 *
 *   public void someMethod() {
 *     LOG.log(LogLevel.ERROR, "Widget %s too large, height=%d", widget, heightCentimeters);
 *   }
 * }
 * ```
 * - Kotlin client
 * ```kotlin
 * private val LOG by lazyLogger(ArtistTable::class) // don't get the logger unless we actually use it
 *
 * class ArtistTable {
 *   fun someMethod(artistId: Long) {
 *     LOG.e { +it("No record for artist _id=%d", artistId) }  // + operator adds call site information
 *   }
 * }
 * ```
 *
 *
 * Created by Eric A. Snell on 6/29/18.
 */
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

  val loggerFQCN: String

  /**
   * Reset the message, ie. `setLength(0)`
   */
  fun reset(): LogEntry

  override fun append(c: Char): LogEntry

  override fun append(csq: CharSequence?): LogEntry

  override fun append(csq: CharSequence?, start: Int, end: Int): LogEntry

  fun append(str: String): LogEntry

  fun append(b: Boolean): LogEntry

  fun append(i: Int): LogEntry

  fun append(lng: Long): LogEntry

  fun append(f: Float): LogEntry

  fun append(d: Double): LogEntry

  /**
   * Use [format] and [args] to format the LogEntry using String.format() type formatting. The
   * resulting String is set into this LogEntry. The underlying logging system sees only a
   * simple string with no parameter substitution
   *
   * If the underlying logging is asynchronous, prefer [log] over this method, which will delay
   * string formatting until the last possible moment (typically on a background thread)
   */
  fun format(format: String, vararg args: Any): LogEntry

  /**
   * Use [locale], [format], and [args] to format the LogEntry using String.format() type
   * formatting. The resulting String is set into this LogEntry. The underlying logging system sees
   * only a simple string with no parameter substitution arguments
   *
   * If the underlying logging is asynchronous, prefer [log] over this method, which will delay
   * string formatting until the last possible moment (typically on a background thread)
   */
  fun format(locale: Locale, format: String, vararg args: Any): LogEntry

  /**
   * Set the [format] and parameter substitution [args] into the this LogEntry for the
   * underlying logging system to format. This is typically done in the [java.text.MessageFormat]
   * style parameter substitution. The [format] strings is retained as-is and the [args] are set
   * into this LogEntry, making both available to the underlying logging system.
   *
   * If the underlying logging is asynchronous, prefer this method over [format], which will delay
   * string formatting until the last possible moment (typically on a background thread)
   */
  fun log(format: String, vararg args: Any): LogEntry

  /**
   * Add the source location, determined by examining the call stack, to the log record. This is
   * an expensive operation as the JVM has to fill out the entire stack frame.
   *
   * @param stackDepth should typically be 0 to add the current location. If layering a
   * facade/adapter over this LogEntry, add 1 to the [stackDepth] for every layer. Searching for
   * class/method names in the [StackTraceElement] list is error prone when extending this
   * framework as the names are unknown to this framework and we do not want to restrict client
   * naming. [stackDepth] is simply an index into the stack trace.
   *
   * @return this LogEntry
   */
  fun addLocation(stackDepth: Int): LogEntry

}
