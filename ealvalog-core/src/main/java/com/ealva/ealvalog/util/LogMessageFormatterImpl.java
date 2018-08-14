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
 * Helper class for formatting a string. This is likely to be a {@link ThreadLocal} passed down into logging layers to help build the
 * message. It's purpose is to reduce the number of {@link StringBuilder} instances and character array allocations/copies
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
public class LogMessageFormatterImpl implements com.ealva.ealvalog.util.LogMessageFormatter {
  private final StringBuilder builder;
  private final Formatter formatter;

  @SuppressWarnings("WeakerAccess") public LogMessageFormatterImpl() {
    builder = new StringBuilder(1024);
    formatter = new Formatter(builder);
  }

  @NotNull @Override
  public com.ealva.ealvalog.util.LogMessageFormatter reset() {
    builder.setLength(0);
    return this;
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(@NotNull final String str) {
    builder.append(str);
    return this;
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(boolean b) {
    builder.append(b);
    return this;
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(final char c) {
    builder.append(c);
    return this;
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(int i) {
    builder.append(i);
    return this;
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(long lng) {
    builder.append(lng);
    return this;
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(float f) {
    builder.append(f);
    return this;
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(double d) {
    builder.append(d);
    return this;
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(@NotNull final String format,
                                                                               @NotNull final Object... args) {
    return append(Locale.getDefault(), format, args);
  }

  @NotNull @Override public com.ealva.ealvalog.util.LogMessageFormatter append(@NotNull Locale locale,
                                                                               @NotNull final String format,
                                                                               @NotNull final Object... args) {
    if (args.length > 0) {
      formatter.format(locale, format, args);
    } else {
      builder.append(format);
    }
    return this;
  }

  @Override public Appendable append(final CharSequence csq) {
    builder.append(csq);
    return this;
  }

  @Override public Appendable append(final CharSequence csq, final int start, final int end) {
    builder.append(csq, start, end);
    return this;
  }

  @Override public String toString() {
    return builder.toString();
  }
}
