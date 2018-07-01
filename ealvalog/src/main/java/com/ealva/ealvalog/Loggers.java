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

import java.util.Formatter;

/**
 * This is where {@link com.ealva.ealvalog.Logger} instances are obtained. This singleton must be configured with a concrete implementation of {@link
 * LoggerFactory} before use.
 * <p>
 * This class has a dependency on a concrete {@link LoggerFactory} instance which must be set before use. Setting and using this factory is
 * the responsibility of the client. It's expected this will be done during application load.
 * <p>
 * Canonical use is to declare a static Logger obtained from a static get() method.
 * <p>
 * <pre>
 * {@code
 * class MyClass {
 *   private static final Logger logger = Loggers.get();
 * }
 * }
 * </pre>
 * <p>
 * As a convenience, static log() methods are provided on this class for "quick and dirty" logging. The Logger to be used is determined from
 * the call stack, which is an expensive operation. Use sparingly and not in time critical areas.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("unused")
public class Loggers {
  private static final int STACK_DEPTH = 1;
  private static volatile LoggerFactory loggerFactory = NullLoggerFactory.INSTANCE;

  /** Set the {@link LoggerFactory} to be used for all calls to this factory */
  public static void setFactory(@NotNull final LoggerFactory factory) {
    loggerFactory = factory;
  }

  /**
   * Get the root logger. Logger with name {@link LoggerFactory#ROOT_LOGGER_NAME}
   *
   * @return the root logger
   */
  public static @NotNull com.ealva.ealvalog.Logger getRoot() {
    return loggerFactory.get(LoggerFactory.ROOT_LOGGER_NAME);
  }

  /**
   * Convenience method to obtain a {@link com.ealva.ealvalog.Logger} for the current object's class. The follow are equivalent:
   * <p>
   * <pre>
   * {@code
   * class MyClass {
   *   private static final Logger logger = Loggers.get();
   * }
   * }
   * class MyClass {
   *   private static final Logger logger = Loggers.get(MyClass.class);
   * }
   * </pre>
   * <p>
   *
   * @return a logger for for the caller's class
   */
  public static @NotNull com.ealva.ealvalog.Logger get() {
    return loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassName(STACK_DEPTH));
  }

  /**
   * Get a {@link com.ealva.ealvalog.Logger} inferring the name from the call stack
   *
   * @param marker each log statement from the returned logger should include this {@link Marker} unless another Marker is used in the log()
   *               call
   *
   * @return logger for the caller's class
   *
   * @see #get()
   */
  public static @NotNull com.ealva.ealvalog.Logger get(final @NotNull Marker marker) {
    return loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassName(STACK_DEPTH), marker);
  }

  /**
   * Get a {@link com.ealva.ealvalog.Logger} for {@code aClass}, using the class name
   *
   * @param aClass logger name comes from the name of this class
   *
   * @return a logger for {@code aClass}
   */
  public static @NotNull com.ealva.ealvalog.Logger get(@NotNull final Class<?> aClass) {
    return loggerFactory.get(aClass.getName());
  }

  /**
   * Get a {@link com.ealva.ealvalog.Logger} for {@code aClass}, using the class name
   *
   * @param name name of the logger. It's assumed this name is a Class name in the standard form
   *
   * @return a logger for {@code name}
   */
  public static @NotNull com.ealva.ealvalog.Logger get(@NotNull final String name) {
    return loggerFactory.get(name);
  }

  /**
   * Get a {@link com.ealva.ealvalog.Logger} for {@code aClass}, using the class name
   *
   * @param aClass logger name comes from the name of this class
   * @param marker each log statement from the returned logger should include this {@link Marker} unless another Marker is used in the log()
   *               call
   *
   * @return a logger for {@code aClass}
   */
  public static @NotNull com.ealva.ealvalog.Logger get(@NotNull final Class<?> aClass, @NotNull final Marker marker) {
    return loggerFactory.get(aClass.getName(), marker);
  }

  /**
   * Get a {@link com.ealva.ealvalog.Logger} for {@code aClass}, using the class name
   *
   * @param name   name of the logger. It's assumed this name is a Class name in the standard form
   * @param marker each log statement from the returned logger should include this {@link Marker} unless another Marker is used in the log()
   *               call
   *
   * @return a logger for {@code name}
   */
  public static @NotNull com.ealva.ealvalog.Logger get(@NotNull final String name, @NotNull final Marker marker) {
    return loggerFactory.get(name, marker);
  }

  /**
   * If isLoggable, log at the {@code msg} at {@code level}
   * <p>
   * Determine which logger to use from call stack and log. Convenience method but slower - don't use in critical path
   *
   * @param level log level to use
   * @param msg   log msg which is unaltered
   */
  public static void log(final @NotNull com.ealva.ealvalog.LogLevel level, final @NotNull String msg) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, msg);
  }

  public static void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull String format, @NotNull Object arg1) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, format, arg1);
  }

  public static void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull String format, @NotNull Object arg1, @NotNull Object arg2) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, format, arg1, arg2);
  }

  public static void log(@NotNull com.ealva.ealvalog.LogLevel level,
                         @NotNull String format,
                         @NotNull Object arg1,
                         @NotNull Object arg2,
                         @NotNull Object arg3) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, format, arg1, arg2, arg3);
  }

  public static void log(@NotNull com.ealva.ealvalog.LogLevel level,
                         @NotNull String format,
                         @NotNull Object arg1,
                         @NotNull Object arg2,
                         @NotNull Object arg3,
                         @NotNull Object arg4) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, format, arg1, arg2, arg3, arg4);
  }

  public static void log(@NotNull com.ealva.ealvalog.LogLevel level,
                         @NotNull String format,
                         @NotNull Object arg1,
                         @NotNull Object arg2,
                         @NotNull Object arg3,
                         @NotNull Object arg4,
                         @NotNull Object... remaining) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, format, arg1, arg2, arg3, arg4, remaining);
  }

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker}
   * <p>
   * Determine which logger to use from call stack and log. Convenience method but slower - don't use in critical path
   *
   * @param level  log level to use
   * @param marker marker to include
   * @param msg    log msg which is unaltered
   */
  public static void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Marker marker, @NotNull String msg) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, marker, msg);
  }

  /**
   * If isLoggable, log at the {@code msg} at {@code level} with the given {@code throwable}
   * <p>
   * Determine which logger to use from call stack and log. Convenience method but slower - don't use in critical path
   *
   * @param level     log level to use
   * @param throwable throwable to include
   * @param msg       log msg which is unaltered
   */
  public static void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Throwable throwable, @NotNull String msg) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, throwable, msg);
  }

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker} and {@code throwable}
   * <p>
   * Determine which logger to use from call stack and log. Convenience method but slower - don't use in critical path
   *
   * @param level     log level to use
   * @param marker    marker to include
   * @param throwable throwable to include
   * @param msg       log msg which is unaltered
   */
  public static void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Marker marker, @NotNull Throwable throwable, @NotNull String msg) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, marker, throwable, msg);
  }

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker}
   * <p>
   * Determine which logger to use from call stack and log. Convenience method but slower - don't use in critical path
   *
   * @param level      log level to use
   * @param marker     marker to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  public static void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Marker marker, @NotNull String format, @NotNull Object... formatArgs) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, marker, format, formatArgs);
  }

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code throwable}
   * <p>
   * Determine which logger to use from call stack and log. Convenience method but slower - don't use in critical path
   *
   * @param level      log level to use
   * @param throwable  throwable to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  public static void log(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Throwable throwable, @NotNull String format, @NotNull Object... formatArgs) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, throwable, format, formatArgs);
  }

  /**
   * If isLoggable, log at the {@code msg} at {@code level} using the {@code marker} and {@code throwable}
   * <p>
   * Determine which logger to use from call stack and log. Convenience method but slower - don't use in critical path
   *
   * @param level      log level to use
   * @param marker     marker to include
   * @param throwable  throwable to include
   * @param format     a format string in the form required by {@link Formatter}
   * @param formatArgs arguments passed to {@link Formatter#format(String, Object...)}
   */
  public static void log(@NotNull com.ealva.ealvalog.LogLevel level,
                         @NotNull Marker marker,
                         @NotNull Throwable throwable,
                         @NotNull String format,
                         @NotNull Object... formatArgs) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).log(level, marker, throwable, format, formatArgs);
  }

  /**
   * Used to log an exception being caught where no message is needed
   * <p>
   * Determine which logger to use from call stack and log. Convenience method but slower - don't use in critical path
   *
   * @param level     log level to use
   * @param throwable the throwable that was caught
   */
  public static void caught(@NotNull com.ealva.ealvalog.LogLevel level, @NotNull Throwable throwable) {
    loggerFactory.get(com.ealva.ealvalog.util.LogUtil.getCallerClassNameStripInner(STACK_DEPTH)).caught(level, throwable);
  }

}
