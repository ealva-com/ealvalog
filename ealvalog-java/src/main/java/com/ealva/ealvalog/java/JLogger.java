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

package com.ealva.ealvalog.java;

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.Marker;

import org.jetbrains.annotations.NotNull;

import java.util.Formatter;
import java.util.function.Supplier;

/**
 * Created by Eric A. Snell on 8/13/18.
 */
public interface JLogger extends Logger {
  boolean isLoggable(@NotNull LogLevel level);

  boolean isLoggable(@NotNull LogLevel level, @NotNull Marker marker);

  boolean isLoggable(@NotNull LogLevel level, @NotNull Throwable throwable);

  /**
   * If isLoggable, log at the {@code msg} at {@code level}
   *
   * @param level log level to use
   * @param msg   log msg which is unaltered
   */
  void log(@NotNull LogLevel level, @NotNull String msg);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker}
   *
   * @param level  log level to use
   * @param marker marker to include
   * @param msg    log msg which is unaltered
   */
  void log(@NotNull LogLevel level, @NotNull Marker marker, @NotNull String msg);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} with the given {@code throwable}
   *
   * @param level     log level to use
   * @param throwable throwable to include
   * @param msg       log msg which is unaltered
   */
  void log(@NotNull LogLevel level, @NotNull Throwable throwable, @NotNull String msg);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker} and
   * {@code throwable}
   *
   * @param level     log level to use
   * @param marker    marker to include
   * @param throwable throwable to include
   * @param msg       log msg which is unaltered
   */
  void log(@NotNull LogLevel level,
           @NotNull Marker marker,
           @NotNull Throwable throwable,
           @NotNull String msg);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker}
   *
   * @param level      log level to use
   * @param marker     marker to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  void log(@NotNull LogLevel level,
           @NotNull Marker marker,
           @NotNull String format,
           @NotNull Object... formatArgs);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code throwable}
   *
   * @param level      log level to use
   * @param throwable  throwable to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  void log(@NotNull LogLevel level,
           @NotNull Throwable throwable,
           @NotNull String format,
           @NotNull Object... formatArgs);

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker} and
   * {@code throwable}
   *
   * @param level      log level to use
   * @param marker     marker to include
   * @param throwable  throwable to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  void log(@NotNull LogLevel level,
           @NotNull Marker marker,
           @NotNull Throwable throwable,
           @NotNull String format,
           @NotNull Object... formatArgs);

  void log(@NotNull LogLevel level, @NotNull String format, @NotNull Object arg1);

  void log(@NotNull LogLevel level,
           @NotNull String format,
           @NotNull Object arg1,
           @NotNull Object arg2);

  void log(@NotNull LogLevel level,
           @NotNull String format,
           @NotNull Object arg1,
           @NotNull Object arg2,
           @NotNull Object arg3);

  void log(@NotNull LogLevel level,
           @NotNull String format,
           @NotNull Object arg1,
           @NotNull Object arg2,
           @NotNull Object arg3,
           @NotNull Object arg4);

  void log(@NotNull LogLevel level,
           @NotNull String format,
           @NotNull Object arg1,
           @NotNull Object arg2,
           @NotNull Object arg3,
           @NotNull Object arg4,
           @NotNull Object... remaining);

  /**
   * Log the result of the supplier, ie supplier.get().toString()
   * @param level level at which to log
   * @param supplier supplies the object whose toString() method will be invoked and logged
   */
  void log(@NotNull LogLevel level, @NotNull Supplier<?> supplier);

  /**
   * Log the result of the supplier and use the Marker to see if it is loggable
   *
   * @param level level at which to log
   * @param marker used to test if loggable
   * @param supplier supplies the object whose toString() method will be invoked and logged
   */
  void log(@NotNull LogLevel level, @NotNull Marker marker, @NotNull Supplier<?> supplier);

  /**
   * Log the result of the supplier and use the Throwable to see if it is loggable
   *
   * @param level level at which to log
   * @param throwable used to test if loggable
   * @param supplier supplies the object whose toString() method will be invoked and logged
   */
  void log(@NotNull LogLevel level,
           @NotNull Throwable throwable,
           @NotNull Supplier<?> supplier);

  /**
   * Log the result of the supplier and use the Marker and Throwable to see if it is loggable
   *
   * @param level level at which to log
   * @param marker used to test if loggable
   * @param throwable used to test if loggable
   * @param supplier supplies the object whose toString() method will be invoked and logged
   */
  void log(@NotNull LogLevel level,
           @NotNull Marker marker,
           @NotNull Throwable throwable,
           @NotNull Supplier<?> supplier);

  /**
   * Used to log an exception being caught where no message is needed. Logs:
   * "Caught: [throwable.getClass().toString()]"
   *
   * @param level     log level to use
   * @param throwable the throwable that was caught
   */
  void caught(@NotNull LogLevel level, @NotNull Throwable throwable);

  /**
   * Log a throwable being thrown at the log site. Logs:
   * "Throwing: [throwable.getClass().toString()]
   * <p><p>
   * <p>
   * {@code
   * throw LOG.throwing(LogLevel.ERROR, new MyException("Important Info"));
   * }
   *
   * @param level     level at which to log
   * @param throwable the Throwable to log
   *
   * @return returns the throwable for convenience
   */
  @NotNull
  <T extends Throwable> T throwing(@NotNull LogLevel level, @NotNull T throwable);

}
