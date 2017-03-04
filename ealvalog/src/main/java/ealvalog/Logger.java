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

package ealvalog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Formatter;

/**
 * It's expected all logging occurs through concrete implementations of this interface which are obtained via {@link TheLoggerFactory}
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
   * Will a log at this {@link Level} result in an actual log statement
   *
   * @param level the level to test, one of {@link Level#TRACE}, {@link Level#DEBUG}, {@link Level#INFO}, {@link Level#WARN}, {@link
   *              Level#ERROR}, {@link Level#CRITICAL}
   *
   * @return true if a log statement will be produced at this level
   */
  boolean isLoggable(@NotNull Level level);

  /**
   * Will a log at this {@link Level}, and with the given {@link Marker}, result in an actual log statement
   *
   * @param level  the level to test, one of {@link Level#TRACE}, {@link Level#DEBUG}, {@link Level#INFO}, {@link Level#WARN}, {@link
   *               Level#ERROR}, {@link Level#CRITICAL}
   * @param marker {@link Marker} to test
   *
   * @return true if a log statement will be produced at this level
   */
  boolean isLoggable(@NotNull Level level, @Nullable Marker marker);

  void log(@NotNull Level level, @NotNull String msg);

  void log(@NotNull Level level, @NotNull Marker marker, @NotNull String msg);

  void log(@NotNull Level level, @NotNull Throwable throwable, @NotNull String msg);

  void log(@NotNull Level level, @NotNull Marker marker, @NotNull Throwable throwable, @NotNull String msg);

  void log(@NotNull Level level, @NotNull Marker marker, @NotNull String format, @NotNull Object... formatArgs);

  void log(@NotNull Level level, @NotNull Throwable throwable, @NotNull String format, @NotNull Object... formatArgs);

  void log(@NotNull Level level,
           @NotNull Marker marker,
           @NotNull Throwable throwable,
           @NotNull String format,
           @NotNull Object... arg);

  void log(@NotNull Level level, @NotNull String format, @NotNull Object arg1);

  void log(@NotNull Level level, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2);

  void log(@NotNull Level level, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2, @NotNull Object arg3);

  void log(@NotNull Level level,
           @NotNull String format,
           @NotNull Object arg1,
           @NotNull Object arg2,
           @NotNull Object arg3,
           @NotNull Object arg4);

  void log(@NotNull Level level,
           @NotNull String format,
           @NotNull Object arg1,
           @NotNull Object arg2,
           @NotNull Object arg3,
           @NotNull Object arg4,
           @NotNull Object... remaining);

  /**
   * Log without checking the the level and indicating where on the call chain the log is occurring ({@code stackDepth}). This method's
   * primary use is for this logging framework and it's not expected client's would typically use this method.
   *
   * @param level log level - will not be checked before logging
   * @param marker an optional {@link Marker}
   * @param throwable an optional {@link Throwable}
   * @param stackDepth the level of indirection from the original "log" invocation. This must be 0 if a client invokes this method.
   * @param msg the log message
   * @param formatArgs any formatting arguments if {@code msg} is a printf style format string (see {@link Formatter}
   */
  void logImmediate(@NotNull Level level,
                    @Nullable Marker marker,
                    @Nullable Throwable throwable,
                    int stackDepth,
                    @NotNull String msg,
                    @NotNull Object... formatArgs);

  /**
   * Log without checking the the level and indicating where on the call chain the log is occurring ({@code stackDepth}). This method's
   * primary use is for this logging framework and it's not expected client's would typically use this method.
   *
   * @param level log level - will not be checked before logging
   * @param throwable an optional {@link Throwable}
   * @param stackDepth the level of indirection from the original "log" invocation. This must be 0 if a client invokes this method.
   * @param msg the log message
   * @param formatArgs any formatting arguments if {@code msg} is a printf style format string (see {@link Formatter}
   */
  void logImmediate(@NotNull Level level,
                    @Nullable Throwable throwable,
                    int stackDepth,
                    @NotNull String msg,
                    @NotNull Object... formatArgs);
}
