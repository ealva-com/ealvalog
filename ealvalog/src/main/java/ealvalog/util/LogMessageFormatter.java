/*
 * Copyright 2017 Eric A. Snell
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

package ealvalog.util;

import org.jetbrains.annotations.NotNull;

import java.util.Formatter;
import java.util.Locale;

/**
 * Helper class for formatting a string. This is likely to be a {@link ThreadLocal} passed down into logging layers to help build the
 * message. It's purpose is to reduce the number of {@link StringBuilder} instances and character array allocations/copies
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
@SuppressWarnings("WeakerAccess")
public class LogMessageFormatter {
  private final StringBuilder builder;
  private final Formatter formatter;

  public LogMessageFormatter() {
    builder = new StringBuilder(1024);
    formatter = new Formatter(builder);
  }

  /**
   * Reset the contained {@link StringBuilder}, ie. {@code setLength(0)}
   */
  public void reset() {
    builder.setLength(0);
  }

  /**
   * Get the builder used by this formatter. Typically used before or after invoking {@link #format(String, Object...)} or
   * {@link #format(Locale, String, Object...)} to add text to the message other than what the original caller specified (eg. Marker or
   *
   * @return this formatter's internal {@link StringBuilder}
   */
  public StringBuilder getBuilder() {
    // TODO: 3/1/17 Expose this or add append() methods to this class? Feels wrong to directly expose the StringBuilder
    return builder;
  }

  /**
   * Format the {@code format} string with the given set of {@code args} into the contained {@link StringBuilder}
   *
   * @param format the format string as defined in {@link Formatter}
   * @param args   arguments pass for {@link Formatter#format(String, Object...)}
   *
   * @return self
   */
  public LogMessageFormatter format(@NotNull final String format,
                                    @NotNull final Object... args) {
    return format(Locale.getDefault(Locale.Category.FORMAT), format, args);
  }

  /**
   * Format the {@code format} string with the given set of {@code args} into the contained {@link StringBuilder}
   *
   * @param locale the {@link Locale} to use during formatting
   * @param format the format string as defined in {@link Formatter}
   * @param args   arguments pass for {@link Formatter#format(String, Object...)}
   *
   * @return self
   */
  @SuppressWarnings("unused")
  public LogMessageFormatter format(@NotNull Locale locale,
                                    @NotNull final String format,
                                    @NotNull final Object... args) {
    if (args.length > 0) {
      formatter.format(locale, format, args);
    } else {
      builder.append(format);
    }
    return this;
  }

  /**
   * @return the result of formatting and appending to the contained {@link StringBuilder}
   */
  public String toString() {
    return builder.toString();
  }
}
