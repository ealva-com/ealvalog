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

package com.ealva.ealvalog.util;

import org.jetbrains.annotations.NotNull;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by Eric A. Snell on 6/27/18.
 */
public interface LogMessageFormatter extends Appendable {
  /**
   * Reset the message, ie. {@code setLength(0)}
   */
  @SuppressWarnings("UnusedReturnValue")
  @NotNull LogMessageFormatter reset();

  @NotNull LogMessageFormatter append(@NotNull String str);

  @NotNull LogMessageFormatter append(boolean b);

  @NotNull LogMessageFormatter append(char c);

  @NotNull LogMessageFormatter append(int i);

  @NotNull LogMessageFormatter append(long lng);

  @NotNull LogMessageFormatter append(float f);

  @NotNull LogMessageFormatter append(double d);

  /**
   * Format the {@code format} string with the given set of {@code args} into the contained {@link StringBuilder}
   *
   * @param format the format string as defined in {@link Formatter}
   * @param args   arguments pass for {@link Formatter#format(String, Object...)}
   *
   * @return self
   */
  @NotNull LogMessageFormatter append(@NotNull String format,
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
  @NotNull LogMessageFormatter append(@NotNull Locale locale,
                                      @NotNull String format,
                                      @NotNull Object... args);

}
