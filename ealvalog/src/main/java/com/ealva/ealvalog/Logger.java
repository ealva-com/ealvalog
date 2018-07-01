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

package com.ealva.ealvalog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Formatter;
import java.util.logging.LogRecord;

/**
 * It's expected all logging occurs through concrete implementations of this interface which are obtained via {@link Loggers}
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public interface Logger {
  /** Used when there are no formatArgs. Expected only use by eAlvaLog framework */
  Object[] NO_ARGUMENTS = new Object[0];

  @NotNull String getName();

  /**
   * @return the current {@link Marker} used by this logger, or null if none set
   */
  @Nullable Marker getMarker();

  /**
   * Set the {@link Marker} to use for all logging from this logger
   *
   * @param marker {@link Marker} to be used with every log, unless overridden per method, or null to remove any current {@link Marker}
   */
  void setMarker(@Nullable Marker marker);

  /**
   * Get the {@link com.ealva.ealvalog.LogLevel} for this Logger. If no LogLevel has been set, null is returned
   *
   * @return the current level or null
   */
  @Nullable com.ealva.ealvalog.LogLevel getLogLevel();

  /**
   * Set an optional LogLevel. If null the nearest ancestor LogLevel will be used as the level for this logger
   * <p>
   * If null is passed to the root logger, it sets the level to {@link com.ealva.ealvalog.LogLevel#NONE}
   *
   * @param logLevel the new level for this logger or removed if null
   */
  void setLogLevel(@Nullable com.ealva.ealvalog.LogLevel logLevel);

  /**
   * Gets the level set for this logger or, if that it has not been set, get the nearest ancestor log level
   *
   * @return the log level at which this logger is operating
   */
  @NotNull com.ealva.ealvalog.LogLevel getEffectLogLevel();

  /**
   * Set if this logger should include call site location information in the log information. This is a relatively expensive operation and
   * defaults to false.
   * <p>
   * The information passed to lower layers of the logging framework include: caller class, caller method, and caller file line number. This
   * is obtained from the call stack. If calling code is obfuscated the results will also be obfuscated.
   * <p>
   * Also note that the underlying log statement formatter must be configured to include this information.
   *
   * @param includeLocation set to true for call site information to be included
   */
  void setIncludeLocation(boolean includeLocation);

  /**
   * @return true if log calls include call site information
   *
   * @see #setIncludeLocation(boolean)
   */
  boolean getIncludeLocation();

  /**
   * Determine if a log call at this {@link com.ealva.ealvalog.LogLevel} will result in an actual log statement. Typically this is only a level check, unless
   * the Logger instance contains a {@link Marker}. In that case the contained Marker is also checked to promote fast short-circuiting
   *
   * @param level the level to test, one of {@link com.ealva.ealvalog.LogLevel#TRACE}, {@link com.ealva.ealvalog.LogLevel#DEBUG}, {@link com.ealva.ealvalog.LogLevel#INFO}, {@link com.ealva.ealvalog.LogLevel#WARN},
   *              {@link com.ealva.ealvalog.LogLevel#ERROR}, {@link com.ealva.ealvalog.LogLevel#CRITICAL}
   *
   * @return true if a log statement will be produced at this level
   */
  boolean isLoggable(@NotNull com.ealva.ealvalog.LogLevel level);

  /**
   * Will a log at this {@link com.ealva.ealvalog.LogLevel}, with the given (optional) {@link Marker} and (optional) {@link Throwable}, result in an actual log
   * statement
   *
   * @param level     the level to test, one of {@link com.ealva.ealvalog.LogLevel#TRACE}, {@link com.ealva.ealvalog.LogLevel#DEBUG}, {@link com.ealva.ealvalog.LogLevel#INFO}, {@link
   *                  com.ealva.ealvalog.LogLevel#WARN}, {@link com.ealva.ealvalog.LogLevel#ERROR}, {@link com.ealva.ealvalog.LogLevel#CRITICAL}
   * @param marker    optional marker to test
   * @param throwable optional throwable to test
   *
   * @return true if a log statement will be produced at this level
   */
  boolean isLoggable(@NotNull com.ealva.ealvalog.LogLevel level, @Nullable Marker marker, @Nullable Throwable throwable);

  /**
   * If isLoggable, log at the {@code msg} at {@code level}
   *
   * @param level log level to use
   * @param msg   log msg which is unaltered
   */
  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull String msg);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker}
   *
   * @param level  log level to use
   * @param marker marker to include
   * @param msg    log msg which is unaltered
   */
  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Marker marker, @NotNull String msg);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} with the given {@code throwable}
   *
   * @param level     log level to use
   * @param throwable throwable to include
   * @param msg       log msg which is unaltered
   */
  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Throwable throwable, @NotNull String msg);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker} and {@code throwable}
   *
   * @param level     log level to use
   * @param marker    marker to include
   * @param throwable throwable to include
   * @param msg       log msg which is unaltered
   */
  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Marker marker, @NotNull Throwable throwable, @NotNull String msg);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker}
   *
   * @param level      log level to use
   * @param marker     marker to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Marker marker, @NotNull String format, @NotNull Object... formatArgs);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code throwable}
   *
   * @param level      log level to use
   * @param throwable  throwable to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Throwable throwable, @NotNull String format, @NotNull Object... formatArgs);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker} and {@code throwable}
   *
   * @param level      log level to use
   * @param marker     marker to include
   * @param throwable  throwable to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  void log(@NotNull com.ealva.ealvalog.LogLevel level,
           @NotNull Marker marker,
           @NotNull Throwable throwable,
           @NotNull String format,
           @NotNull Object... formatArgs);

  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull String format, @NotNull Object arg1);

  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2);

  void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2, @NotNull Object arg3);

  void log(@NotNull com.ealva.ealvalog.LogLevel level,
           @NotNull String format,
           @NotNull Object arg1,
           @NotNull Object arg2,
           @NotNull Object arg3,
           @NotNull Object arg4);

  void log(@NotNull com.ealva.ealvalog.LogLevel level,
           @NotNull String format,
           @NotNull Object arg1,
           @NotNull Object arg2,
           @NotNull Object arg3,
           @NotNull Object arg4,
           @NotNull Object... remaining);

  /**
   * Used to log an exception being caught where no message is needed
   *
   * @param level     log level to use
   * @param throwable the throwable that was caught
   */
  void caught(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Throwable throwable);

  /**
   * Log a throwable being thrown at the log site.
   * <p>
   * {@code
   * throw LOG.throwing(LogLevel.ERROR, new MyException("Important Info"));
   * }
   *
   * @param level     level at which to log
   * @param throwable the Throwable/Exception/Error to log
   *
   * @return returns the throwable for convenience
   */
  Throwable throwing(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Throwable throwable);

  /**
   * Log without checking the the level and indicate where on the call chain the log is occurring ({@code stackDepth}). This method's
   * primary use is for this logging framework and it's not expected client's would typically use this method.
   * <p>
   * This method has a {@link Marker} parameter which will override any {@link Marker} in the Logger itself
   *
   * @param level      log level - will not be checked before logging
   * @param marker     an optional {@link Marker}
   * @param throwable  an optional {@link Throwable}
   * @param stackDepth the level of indirection from the original "log" invocation. This must be 0 if a client invokes this method.
   * @param msg        the log message
   * @param formatArgs any formatting arguments if {@code msg} is a printf style format string (see {@link Formatter}
   */
  void logImmediate(@NotNull com.ealva.ealvalog.LogLevel level,
                    @Nullable Marker marker,
                    @Nullable Throwable throwable,
                    int stackDepth,
                    @NotNull String msg,
                    @NotNull Object... formatArgs);

  /**
   * Log without checking the the level and indicate where on the call chain the log is occurring ({@code stackDepth}). This method's
   * primary use is for this logging framework and it's not expected client's would typically use this method.
   *
   * @param level      log level - will not be checked before logging
   * @param throwable  an optional {@link Throwable}
   * @param stackDepth the level of indirection from the original "log" invocation. This must be 0 if a client invokes this method.
   * @param msg        the log message
   * @param formatArgs any formatting arguments if {@code msg} is a printf style format string (see {@link Formatter}
   */
  void logImmediate(@NotNull com.ealva.ealvalog.LogLevel level,
                    @Nullable Throwable throwable,
                    int stackDepth,
                    @NotNull String msg,
                    @NotNull Object... formatArgs);

  /**
   * Log without checking the the level. This method's
   * primary use is for this logging framework and it's not expected client's would typically use this method.
   *
   * @param record the full log information
   */
  void logImmediate(@NotNull LogRecord record);
}
