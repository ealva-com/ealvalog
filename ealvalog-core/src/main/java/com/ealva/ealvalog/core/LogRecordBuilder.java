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

package com.ealva.ealvalog.core;

import org.jetbrains.annotations.NotNull;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Eric A. Snell on 6/29/18.
 */
@SuppressWarnings("unused")
public interface LogRecordBuilder extends Appendable {
  /**
   * Reset the message, ie. {@code setLength(0)}
   */
  @NotNull LogRecordBuilder reset();

  @NotNull LogRecordBuilder append(@NotNull String str);

  @NotNull LogRecordBuilder append(boolean b);

  @NotNull LogRecordBuilder append(char c);

  @NotNull LogRecordBuilder append(int i);

  @NotNull LogRecordBuilder append(long lng);

  @NotNull LogRecordBuilder append(float f);

  @NotNull LogRecordBuilder append(double d);

  /**
   * Format the {@code format} string with the given set of {@code args} into the contained {@link StringBuilder}
   *
   * @param format the format string as defined in {@link Formatter}
   * @param args   arguments pass for {@link Formatter#format(String, Object...)}
   *
   * @return self
   */
  @NotNull LogRecordBuilder append(@NotNull String format,
                                   @NotNull Object... args);

  /**
   * Format the {@code format} string with the given set of {@code args} into the contained {@link StringBuilder}
   *
   * @param locale the {@link Locale} to use during formatting
   * @param format the format string as defined in {@link Formatter}
   * @param args   arguments pass for {@link Formatter#format(String, Object...)}
   *
   * @return self
   */
  @NotNull LogRecordBuilder append(@NotNull Locale locale,
                                   @NotNull String format,
                                   @NotNull Object... args);

  /**
   * Add the source location, determined by examining the call stack, to the log record. This is
   * an expensive operation as the JVM has to fill out the entire stack frame.
   *
   * @param stackDepth should typically be 0 to add the current location
   *
   * @return this LogRecordBuilder
   */
  @SuppressWarnings("unused")
  @NotNull LogRecordBuilder addLocation(int stackDepth);
}
