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

package ealvalog.base;

import ealvalog.LogLevel;
import ealvalog.Logger;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BaseLogger provides MessageFormatter, consolidates various logging methods to one method after checking log level and obtaining
 * optional info such as log() invocation site.
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseLogger implements Logger {
  private static final int STACK_DEPTH = 1;

  @Nullable private Marker marker;

  protected BaseLogger() {
    this.marker = null;
  }

  protected BaseLogger(@Nullable final Marker marker) {
    this.marker = marker;
  }

  @Nullable @Override public Marker getMarker() {
    return marker;
  }

  @Override public void setMarker(@Nullable final Marker marker) {
    this.marker = marker;
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level) {
    return isLoggable(level, marker);
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final String msg) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, null, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final Marker marker, @NotNull final String msg) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, null, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final Throwable throwable, @NotNull final String msg) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String msg) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, throwable, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1, arg2);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3) {
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, arg1, arg2, arg3);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3,
                            @NotNull final Object arg4) {
    if (isLoggable(level, marker)) {
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
    if (isLoggable(level, marker)) {
      logImmediate(level, marker, null, STACK_DEPTH, format, LogUtil.combineArgs(remaining, arg1, arg2, arg3, arg4));
    }
  }

  @Override public void logImmediate(@NotNull final LogLevel level,
                                     @Nullable final Marker marker,
                                     @Nullable final Throwable throwable,
                                     final int stackDepth,
                                     @NotNull final String msg,
                                     @NotNull final Object... formatArgs) {
    printLog(level, marker, throwable, getCallSite(stackDepth + 1, level, marker, throwable), msg, formatArgs);
  }

  @Override
  public void logImmediate(@NotNull final LogLevel level,
                           @Nullable final Throwable throwable,
                           final int stackDepth,
                           @NotNull final String msg,
                           final @NotNull Object... formatArgs) {
    printLog(level, marker, throwable, getCallSite(stackDepth + 1, level, marker, throwable), msg, formatArgs);
  }

  /**
   * All logging funnels through here. Subclasses implement this method to perform the actual concrete logging. Level has been checked so
   * this method should proceed as logging will occur.
   * <p>
   * Note: even though a particular implementation of this class returns false from {@link #shouldIncludeLocation(LogLevel, Marker,
   * Throwable)}, this method might still receive a valid {@link StackTraceElement} instance if another part of the framework required the
   * information. So though unexpected, implementations may still wish to make use of the information if available.
   *
   * @param level          the log level
   * @param marker         an optional {@link Marker}
   * @param throwable      an optional {@link Throwable}
   * @param callerLocation optional call site information
   * @param msg            the message or format string passed by the client
   * @param formatArgs     any format arguments passed by the client. Never null but may be zero length if no formatting is necessary
   */
  protected abstract void printLog(@NotNull final LogLevel level,
                                   @Nullable final Marker marker,
                                   @Nullable final Throwable throwable,
                                   @Nullable final StackTraceElement callerLocation,
                                   @NotNull final String msg,
                                   @NotNull final Object... formatArgs);

  /**
   * Does this Logger implementation want information about where the log method was invoked. This may be a relatively expensive operation.
   * If a Throwable is included in the log information it might be enough information, though the throw site may drastically differ from
   * the log site.
   *
   * @return true if the log information should include a {@link StackTraceElement} indicating the call site of the log invocation.
   */
  protected abstract boolean shouldIncludeLocation(@NotNull LogLevel level, @Nullable Marker marker, @Nullable final Throwable throwable);

  protected StackTraceElement getCallSite(final int currentStackDepthFromCallSite,
                                          @NotNull final LogLevel level,
                                          @Nullable final Marker marker,
                                          @Nullable final Throwable throwable) {
    if (shouldIncludeLocation(level, marker, throwable)) {
      return LogUtil.getCallerLocation(currentStackDepthFromCallSite + 1);
    }
    return null;
  }

}
