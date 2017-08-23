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

import static ealvalog.util.LogUtil.combineArgs;

/**
 * This class is provided as a convenience base class to customize the logger interface. Subclasses can provide a very specific interface of
 * logging taking combinations of primitives and objects, without incurring object creation overhead when logging does not occur.
 * <p>
 * Our experience with production systems, online and batch, has shown us many examples of developers not logging as appropriate or
 * introducing conditional logic because the code path was time critical and trace/debug/info level logging was causing performance problems
 * when not being used. This is especially true in resource constrained environments, such as Android.
 * <p>
 * If the client has numerous low level log statements, eg. {@link LogLevel#TRACE}, {@link LogLevel#DEBUG}, {@link LogLevel#INFO}, etc, and
 * many primitives are logged, this may cause a lot of unnecessary object creation due to autoboxing. If the log level is high, such as
 * {@link LogLevel#ERROR}, every call to log at lower levels will require object creation, though the logging may actually never occur. This
 * class, and it's subclasses (developed by you), gives you the trade-off between an extra method invocation or 'n' number of autoboxing
 * objects created. An small example class is provided.
 * <p>
 * While completely unnecessary to use eAlvaLog, it's expected a client might implement some small number of project specific logging
 * methods and possibly only for areas measured as time critical. Given that most of the work is done via this well tested framework, there
 * is little downside and can be a huge upside to some small custom implementations.
 * <p>
 * In the next example, 2 object creations would be saved if no logging actually occurred when this method was invoked.
 * <p> <pre>
 * {@code
 * <p>
 * public void log(final Level level, final String format, final long arg1, final long arg2) {
 *   if (realLogger.isLoggable(level)) {
 *     realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2);
 *   }
 * }
 * }
 * </pre>
 * <p>
 * <p>
 * In the next example with 5 arguments, 6 object creations would be saved if the logging did not occur: 5 auto boxing and an Object[]
 * allocation. This is compared to the general framework call {@link #log(LogLevel, String, Object, Object, Object, Object, Object...)}
 * <p>
 * <p><pre>
 * {@code
 * <p>
 * public void log(final Level level,
 *                 final String format,
 *                 final long arg1,
 *                 final double arg2,
 *                 final boolean arg3,
 *                 final long arg4,
 *                 final double arg5) {
 *   if (realLogger.isLoggable(level)) {
 *     realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2, arg3, arg4, arg5);
 *   }
 * }
 * }
 * </pre>
 * <p>
 * Created by Eric A. Snell on 3/3/17.
 */
@SuppressWarnings("unused")
public abstract class LoggerWrapper implements Logger {
  @SuppressWarnings("WeakerAccess") protected final int STACK_DEPTH = 2;
  @SuppressWarnings("WeakerAccess") protected final Logger realLogger;

  /** @throws IllegalArgumentException if realLogger is null */
  public LoggerWrapper(final Logger realLogger) {
    if (realLogger == null) {
      throw new IllegalArgumentException("realLogger cannot be null");
    }
    this.realLogger = realLogger;
  }

  @Override @NotNull public String getName() {
    return realLogger.getName();
  }

  @Override @Nullable public Marker getMarker() {
    return realLogger.getMarker();
  }

  @Override public void setMarker(@Nullable final Marker marker) {
    realLogger.setMarker(marker);
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level) {
    return realLogger.isLoggable(level);
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final String msg) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final Marker marker, @NotNull final String msg) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, marker, null, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final Throwable throwable, @NotNull final String msg) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, throwable, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String msg) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, marker, throwable, STACK_DEPTH, msg, NO_ARGUMENTS);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, marker, null, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, throwable, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object[] formatArgs) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, marker, throwable, STACK_DEPTH, format, formatArgs);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2, arg3);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3,
                            @NotNull final Object arg4) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, arg1, arg2, arg3, arg4);
    }
  }

  @Override public void log(@NotNull final LogLevel level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3,
                            @NotNull final Object arg4,
                            @NotNull final Object... remaining) {
    if (realLogger.isLoggable(level)) {
      realLogger.logImmediate(level, null, STACK_DEPTH, format, combineArgs(remaining, arg1, arg2, arg3, arg4));
    }
  }

  @Override public void logImmediate(@NotNull final LogLevel level,
                                     @Nullable final Marker marker,
                                     @Nullable final Throwable throwable,
                                     final int stackDepth,
                                     @NotNull final String msg,
                                     @NotNull final Object... formatArgs) {
    realLogger.logImmediate(level, marker, throwable, stackDepth, msg, formatArgs);
  }

  @Override public void logImmediate(@NotNull final LogLevel level,
                                     @Nullable final Throwable throwable,
                                     final int stackDepth,
                                     @NotNull final String msg,
                                     final @NotNull Object[] formatArgs) {
    realLogger.logImmediate(level, throwable, stackDepth, msg, formatArgs);
  }

  @Nullable @Override public LogLevel getLogLevel() {
    return realLogger.getLogLevel();
  }

  @Override public void setLogLevel(@Nullable final LogLevel logLevel) {
    realLogger.setLogLevel(logLevel);
  }

  @NotNull @Override public LogLevel getEffectLogLevel() {
    return realLogger.getEffectLogLevel();
  }

  @Override public void setIncludeLocation(final boolean includeLocation) {
    realLogger.setIncludeLocation(includeLocation);
  }

  @Override public boolean getIncludeLocation() {
    return realLogger.getIncludeLocation();
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level, @Nullable final Marker marker, @Nullable final Throwable throwable) {
    return realLogger.isLoggable(level, marker, throwable);
  }

  @Override public void caught(@NotNull final LogLevel level, @NotNull final Throwable throwable) {
    realLogger.caught(level, throwable);
  }

  @Override public Throwable throwing(@NotNull final LogLevel level, @NotNull final Throwable throwable) {
    return realLogger.throwing(level, throwable);
  }
}
