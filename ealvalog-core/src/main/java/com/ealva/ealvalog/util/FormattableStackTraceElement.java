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

import org.jetbrains.annotations.Nullable;

import static java.util.FormattableFlags.ALTERNATE;
import static java.util.FormattableFlags.LEFT_JUSTIFY;
import static java.util.FormattableFlags.UPPERCASE;

import java.util.Formattable;
import java.util.Formatter;

/**
 * Formats a contained {@link StackTraceElement}
 *
 * Created by Eric A. Snell on 3/15/17.
 */
public class FormattableStackTraceElement implements Formattable {
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

  private @Nullable StackTraceElement element;

  private FormattableStackTraceElement(@Nullable final StackTraceElement element) {
    this.element = element;
  }

  @Nullable public StackTraceElement getElement() {
    return element;
  }

  public void setElement(@Nullable final StackTraceElement element) {
    this.element = element;
  }

  @Override public void formatTo(final Formatter formatter, final int flags, final int width, final int precision) {
    if (element != null) {
      final boolean useAlternate = (flags & ALTERNATE) == ALTERNATE;
      final boolean leftJustify = (flags & LEFT_JUSTIFY) == LEFT_JUSTIFY;
      final boolean upperCase = (flags & UPPERCASE) == UPPERCASE;
      final StringBuilder builder = threadLocalStringBuilder.get();
      if (useAlternate) {
        builder.append(element.getClassName())
               .append('.');
      }
      builder.append(element.getMethodName())
             .append(':')
             .append(element.getLineNumber());
      if (precision != -1 && builder.length() + 2 > precision) {
        final int amount = builder.length() + 3 - precision;
        builder.delete(0, amount);
        builder.insert(0, '…');
      }
      builder.insert(0, '(');
      builder.append(')');
      if (width != -1 && width > builder.length()) {
        builder.ensureCapacity(width);
        final int padAmount = width - builder.length();
        if (leftJustify) {
          for (int i = 0; i < padAmount; i++) {
            builder.append(' ');
          }
        } else {
          for (int i = 0; i < padAmount; i++) {
            builder.insert(0, ' ');
          }
        }
      }
      formatter.format(upperCase ? builder.toString().toUpperCase() : builder.toString());
    } else {
      formatter.format("");
    }

  }

  @SuppressWarnings("SameParameterValue")
  public static FormattableStackTraceElement make(final StackTraceElement location) {
    return new FormattableStackTraceElement(location);
  }
}
