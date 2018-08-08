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

import java.util.Formatter
import java.util.Locale

/**
 * Created by Eric A. Snell on 6/29/18.
 */
interface LogRecordBuilder : Appendable {
  /**
   * Reset the message, ie. `setLength(0)`
   */
  fun reset(): LogRecordBuilder

  fun append(str: String): LogRecordBuilder

  fun append(b: Boolean): LogRecordBuilder

  override fun append(c: Char): LogRecordBuilder

  fun append(i: Int): LogRecordBuilder

  fun append(lng: Long): LogRecordBuilder

  fun append(f: Float): LogRecordBuilder

  fun append(d: Double): LogRecordBuilder

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
  ): LogRecordBuilder

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
  ): LogRecordBuilder

  /**
   * Add the source location, determined by examining the call stack, to the log record. This is
   * an expensive operation as the JVM has to fill out the entire stack frame.
   *
   * @param stackDepth should typically be 0 to add the current location
   *
   * @return this LogRecordBuilder
   */
  fun addLocation(stackDepth: Int): LogRecordBuilder
}
