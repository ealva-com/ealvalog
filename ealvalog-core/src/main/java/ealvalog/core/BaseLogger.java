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

package ealvalog.core;

import ealvalog.LogLevel;
import ealvalog.Logger;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BaseLogger consolidates various logging methods to one method after checking log level
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseLogger implements Logger {
  private static final int STACK_DEPTH = 1;

  @NotNull private final String name;
  @Nullable private Marker marker;

  @SuppressWarnings("unused")
  protected BaseLogger(final @NotNull String name) {
    this(name, null);
  }

  protected BaseLogger(final @NotNull String name, final @Nullable Marker marker) {
    this.name = name;
    this.marker = marker;
  }

  @Override public @NotNull String getName() {
    return name;
  }

  @Nullable @Override public Marker getMarker() {
    return marker;
  }

  @Override public void setMarker(@Nullable final Marker marker) {
    this.marker = marker;
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level) {
    return isLoggable(level, marker, null);
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final String msg) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final Marker marker, @NotNull final String msg) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final Throwable throwable, @NotNull final String msg) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String msg) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1, arg2);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1, arg2, arg3);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3,
                            @NotNull final Object arg4) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1, arg2, arg3, arg4);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3,
                            @NotNull final Object arg4,
                            @NotNull final Object... remaining) {
    if (isLoggable(level, marker, null)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, ealvalog.util.LogUtil.combineArgs(remaining, arg1, arg2, arg3, arg4));
    }
  }

  @Override public void caught(@NotNull final LogLevel level, @NotNull final Throwable throwable) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, throwable.getLocalizedMessage());
    }
  }

  @Override public Throwable throwing(@NotNull final LogLevel level, @NotNull final Throwable throwable) {
    if (isLoggable(level, marker, throwable)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, throwable.getLocalizedMessage());
    }
    return throwable;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This version passes an optional marker through to the underlying logger. Even if this null, it will override any contained {@link
   * Marker}
   */
  @Override public void logImmediate(@NotNull final LogLevel level,
                                     @Nullable final Marker marker,
                                     @Nullable final Throwable throwable,
                                     final int stackDepth,
                                     @NotNull final String msg,
                                     @NotNull final Object... formatArgs) {
    printLog(level, marker, throwable, stackDepth + 1, msg, formatArgs);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This version passes down any contained Marker
   */
  @Override
  public void logImmediate(@NotNull final LogLevel level,
                           @Nullable final Throwable throwable,
                           final int stackDepth,
                           @NotNull final String msg,
                           final @NotNull Object... formatArgs) {
    printLog(level, marker, throwable, stackDepth + 1, msg, formatArgs);
  }

  /**
   * All logging funnels through here. Subclasses implement this method to perform the actual concrete logging. {@link #isLoggable(LogLevel,
   * Marker, Throwable)} has been checked so this method should proceed as if logging should occur.
   *
   * @param level      the log level
   * @param marker     an optional {@link Marker}
   * @param throwable  an optional {@link Throwable}
   * @param stackDepth depth from original log call
   * @param msg        the message or format string passed by the client
   * @param formatArgs any format arguments passed by the client. Never null but may be zero length if no formatting is necessary
   */
  protected abstract void printLog(@NotNull final LogLevel level,
                                   @Nullable final Marker marker,
                                   @Nullable final Throwable throwable,
                                   final int stackDepth,
                                   @NotNull final String msg,
                                   @NotNull final Object... formatArgs);

}
