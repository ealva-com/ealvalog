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

/**
 * Creates {@link Logger} instances, typically based on the class which will be logging.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public interface LoggerFactory {
  String ROOT_LOGGER_NAME = "";

  /**
   * Get a Logger instance with the given {@code name}
   *
   * @param name the name of the logger. This name will be treated as if it were a class name with the canonical package hierarchy
   *
   * @return {@link Logger} instance
   */
  @NotNull Logger get(@NotNull String name);

  /**
   * Get a Logger instance with the given {@code name}
   *
   * @param name            the name of the logger. This name will be treated as if it were a class name with the canonical package
   *                        hierarchy
   * @param includeLocation include call site location in every log call. Formatters must be configured to display this optional
   *                        information.
   *
   * @return {@link Logger} instance
   */
  @NotNull Logger get(@NotNull String name, boolean includeLocation);

  /**
   * Get a Logger instance with the given {@code name} that always logs {@code marker}
   *
   * @param name   the name of the logger. This name will be treated as if it were a class name with the canonical package hierarchy
   * @param marker every log from the returned {@link Logger} will use this as it's {@link Marker} unless overridden on a per method basis
   *
   * @return {@link Logger} instance
   *
   * @see Logger#log(LogLevel, String)
   * @see Logger#log(LogLevel, Marker, String)
   */
  @NotNull Logger get(@NotNull String name, @NotNull Marker marker);

  /**
   * Get a Logger instance with the given {@code name} that always logs {@code marker}
   *
   * @param name   the name of the logger. This name will be treated as if it were a class name with the canonical package hierarchy
   * @param marker every log from the returned {@link Logger} will use this as it's {@link Marker} unless overridden on a per method basis
   * @param includeLocation include call site location in every log call. Formatters must be configured to display this optional
   *                        information.
   *
   * @return {@link Logger} instance
   *
   * @see Logger#log(LogLevel, String)
   * @see Logger#log(LogLevel, Marker, String)
   */
  @NotNull Logger get(@NotNull String name, @NotNull Marker marker, boolean includeLocation);
}
