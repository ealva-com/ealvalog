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
  /**
   * Make a Logger instance with the given {@code name}
   *
   * @param name the name of the logger. This name will be treated as if it were a class name with the canonical package hierarchy
   *
   * @return {@link Logger} instance
   */
  @NotNull Logger make(@NotNull String name);

  /**
   * Make a Logger instance with the given {@code name} that always logs {@code marker}
   *
   * @param name   the name of the logger. This name will be treated as if it were a class name with the canonical package hierarchy
   * @param marker every log from the returned {@link Logger} will use this as it's {@link Marker} unless overridden on a per method basis
   *
   * @return {@link Logger} instance
   * @see Logger#log(Level, String)
   * @see Logger#log(Level, Marker, String)
   */
  @NotNull Logger make(@NotNull String name, @NotNull Marker marker);
}
