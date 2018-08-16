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

import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.util.LogUtil;

import org.jetbrains.annotations.NotNull;

/**
 * This is where JLogger instances are obtained. This singleton is configured to use a
 * JLoggerFactoryImpl
 * <p>
 * If clients need specialized logging, such as log methods which take primitives to avoid
 * object creation in time critical areas, the client should create a subclass of
 * JLoggerImpl and create a JLoggerFactory which returns instances of the JLoggerImpl
 * subclass. Then create a singleton  which uses the specialized subclasses.
 * For most java libraries or apps, this JLoggers should suffice.
 * <p>
 * Created by Eric A. Snell on 8/13/2018.
 */
@SuppressWarnings("WeakerAccess")
public final class JLoggers {
  private static JLoggerFactory<JLogger> factory = new JLoggerFactoryImpl();

  private JLoggers() {}

  @NotNull
  public static JLogger getRoot() {
    return factory.get("", null, false);
  }

  @NotNull
  public static JLogger get(final @NotNull String name) {
    return factory.get(name, null, false);
  }

  @NotNull
  public static JLogger get(final @NotNull String name, final @NotNull Marker marker) {
    return factory.get(name, marker, false);
  }

  @NotNull
  public static JLogger get(final @NotNull String name,
                            final @NotNull Marker marker,
                            final boolean includeLocation) {
    return factory.get(name, marker, includeLocation);
  }

  @NotNull
  public static JLogger get(final @NotNull Class clazz) {
    return factory.get(clazz.getName(), null, false);
  }

  @NotNull
  public static JLogger get(final @NotNull Class clazz, final @NotNull Marker marker) {
    return factory.get(clazz.getName(), marker, false);
  }

  @NotNull
  public static JLogger get(final @NotNull Class clazz,
                            final @NotNull Marker marker,
                            final boolean includeLocation) {
    return factory.get(clazz.getName(), marker, includeLocation);
  }

  @NotNull
  public static JLogger get() {
    return factory.get(LogUtil.getCallerClassName(1), null, false);
  }

  @NotNull
  public static JLogger get(final @NotNull Marker marker) {
    return factory.get(LogUtil.getCallerClassName(1), marker, false);
  }

  @NotNull
  public static JLogger get(final @NotNull Marker marker, final boolean includeLocation) {
    return factory.get(LogUtil.getCallerClassName(1), marker, includeLocation);
  }
}
