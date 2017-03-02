/*
 * Copyright 2017 Eric A. Snell
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

/**
 * This is where {@link Logger} instances are obtained. This singleton must be configured with a concrete implementation of
 * {@link LoggerFactory} before use.
 * <p>
 * This class has a dependency on a concrete {@link LoggerFactory} instance which must be set before use. Setting and using this factory
 * is not thread safe - this is the responsibility of the client. It's expected this will be done during application load.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("unused")
public class TheLoggerFactory {
  private static LoggerFactory loggerFactory = NullLoggerFactory.INSTANCE;

  public static void setFactory(@NotNull final LoggerFactory factory) {
    loggerFactory = factory;
  }

  @NotNull
  public static Logger make(@NotNull final Class<?> aClass) {
    return loggerFactory.make(aClass.getName());
  }

  @NotNull
  public static Logger make(@NotNull final String name) {
    return loggerFactory.make(name);
  }

  @NotNull
  public static Logger make(@NotNull final Class<?> aClass, @NotNull final Marker marker) {
    return loggerFactory.make(aClass.getName(), marker);
  }

  @NotNull
  public static Logger make(@NotNull final String name, @NotNull final Marker marker) {
    return loggerFactory.make(name, marker);
  }


}
