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

package ealvalog.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.FormattableFlags.ALTERNATE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Contains an optional {@link Throwable} for formatting on log record output. Will format to {@link Throwable#toString()} except if the
 * {@link FormattableFlags#ALTERNATE} is specified ($#), then the stack trace is included.
 * <p>
 * Created by Eric A. Snell on 3/8/17.
 */
@SuppressWarnings("unused")
public class FormattableThrowable extends Throwable implements Formattable {

  /**
   * Converts a {@link Throwable} for a {@code FormattableThrowable} if necessary
   *
   * @param throwable to be possibly wrapped. If instanceof FormmatableThrowable returned unchanged
   *
   * @return the original {@link Throwable} wrapped in a {@code FormattableThrowable}, or the original {@code throwable} if it's already a
   * {@code FormattableThrowable}
   */
  public static @NotNull FormattableThrowable throwableToFormattable(final @Nullable Throwable throwable) {
    if (throwable instanceof FormattableThrowable) {
      return (FormattableThrowable)throwable;
    } else {
      return new FormattableThrowable(throwable);
    }
  }

  private @Nullable Throwable realThrowable;

  public FormattableThrowable(final @Nullable Throwable throwable) {
    realThrowable = throwable;
  }

  @Override public void formatTo(final Formatter formatter, final int flags, final int width, final int precision) {
    if (realThrowable != null) {
      if ((flags & ALTERNATE) == ALTERNATE) {
        formatter.format(getStackTraceAsString(realThrowable));
      } else {
        formatter.format(realThrowable.toString());
      }
    } else {
      formatter.format("");
    }
  }

  @Override public String getMessage() {
    return realThrowable != null ? realThrowable.getMessage() : "";
  }

  @Override public String toString() {
    return realThrowable != null ? realThrowable.toString() : "";
  }

  public @Nullable Throwable getRealThrowable() {
    return realThrowable;
  }

  public FormattableThrowable setRealThrowable(final @Nullable Throwable realThrowable) {
    this.realThrowable = realThrowable;
    return this;
  }

  private static String getStackTraceAsString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }
}
