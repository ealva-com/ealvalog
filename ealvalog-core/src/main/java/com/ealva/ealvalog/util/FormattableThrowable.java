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
import org.jetbrains.annotations.Nullable;

import static java.util.FormattableFlags.ALTERNATE;
import static java.util.FormattableFlags.LEFT_JUSTIFY;
import static java.util.FormattableFlags.UPPERCASE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Contains an optional {@link Throwable} for formatting on log record output. Will format to {@link Throwable#toString()} except if the
 * {@link FormattableFlags#ALTERNATE} is specified ($#), then the stack trace is included.
 * <p>
 * Created by Eric A. Snell on 3/8/17.
 */
@SuppressWarnings("unused")
public class FormattableThrowable extends BaseFormattable {
  private static final ThreadLocal<StringBuilder> threadLocalStringBuilder =
      new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
          return new StringBuilder(1024);
        }

        @Override
        public StringBuilder get() {
          StringBuilder builder = super.get();
          builder.setLength(0);
          return builder;
        }
      };

  @SuppressWarnings("SameParameterValue")
  public static @NotNull FormattableThrowable make(final @Nullable Throwable throwable) {
    return new FormattableThrowable(throwable);
  }

  private @Nullable Throwable realThrowable;

  private FormattableThrowable(final @Nullable Throwable throwable) {
    realThrowable = throwable;
  }

  @Override
  public void formatTo(final Formatter formatter,
                       final int flags,
                       final int width,
                       final int precision) {
    final boolean useAlternate = (flags & ALTERNATE) == ALTERNATE;
    final boolean leftJustify = (flags & LEFT_JUSTIFY) == LEFT_JUSTIFY;
    final boolean upperCase = (flags & UPPERCASE) == UPPERCASE;
    final StringBuilder builder = threadLocalStringBuilder.get();

    if (realThrowable != null) {
      if (useAlternate) {
        getStackTraceAsString(builder, realThrowable);
      } else {
        builder.append(realThrowable.toString());
      }
      if (precision != -1 && builder.length() > precision) {
        builder.setLength(precision - 1);
        builder.append('…');
      }
    } else {
      formatter.format("");
    }
    maybePadAndJustify(width, leftJustify, builder);
    formatter.format(upperCase ? builder.toString().toUpperCase() : builder.toString());
  }

  public @Nullable Throwable getRealThrowable() {
    return realThrowable;
  }

  public void setRealThrowable(final @Nullable Throwable realThrowable) {
    this.realThrowable = realThrowable;
  }

  /**
   * Later we may find a better way to do this, such as a PrintWriter impl that writes to a StringBuilder. So, we'll pass in the
   * StringBuilder to this method instead of returning a string.
   */
  private static void getStackTraceAsString(final StringBuilder builder, Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    builder.append(stringWriter.toString());
  }
}
