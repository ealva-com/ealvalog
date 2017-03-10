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

import ealvalog.base.LogUtil;
import org.jetbrains.annotations.NotNull;

/**
 * This is where {@link Logger} instances are obtained. This singleton must be configured with a concrete implementation of
 * {@link LoggerFactory} before use.
 * <p>
 * This class has a dependency on a concrete {@link LoggerFactory} instance which must be set before use. Setting and using this factory
 * is the responsibility of the client. It's expected this will be done during application load.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("unused")
public class TheLoggerFactory {
  private static final int STACK_DEPTH = 1;
  private static volatile LoggerFactory loggerFactory = NullLoggerFactory.INSTANCE;

  /** Set the {@link ealvalog.LoggerFactory} to be used for all calls to this factory */
  public static void setFactory(@NotNull final LoggerFactory factory) {
    loggerFactory = factory;
  }

  public static @NotNull Logger getRoot() {
    return loggerFactory.get(LoggerFactory.ROOT_LOGGER_NAME);
  }

  /**
   * Convenience method to obtain a {@link Logger} for the current object's class. The follow are equivalent:
   * <p>
   * <pre>
   * {@code
   * class MyClass {
   *   private static final Logger logger = TheLoggerFactory.get();
   * }
   * }
   * class MyClass {
   *   private static final Logger logger = TheLoggerFactory.get(MyClass.class);
   * }
   * </pre>
   * <p>
   * @return a logger for the current object's Class
   */
  public static @NotNull Logger get() {
    return loggerFactory.get(LogUtil.getCallerLocation(STACK_DEPTH).getClassName());
  }

  public static @NotNull Logger get(final @NotNull Marker marker) {
    return loggerFactory.get(LogUtil.getCallerLocation(STACK_DEPTH).getClassName(), marker);
  }

  public static @NotNull Logger get(@NotNull final Class<?> aClass) {
    return loggerFactory.get(aClass.getName());
  }

  public static @NotNull Logger get(@NotNull final String name) {
    return loggerFactory.get(name);
  }

  public static @NotNull Logger get(@NotNull final Class<?> aClass, @NotNull final Marker marker) {
    return loggerFactory.get(aClass.getName(), marker);
  }

  public static @NotNull Logger get(@NotNull final String name, @NotNull final Marker marker) {
    return loggerFactory.get(name, marker);
  }

}
