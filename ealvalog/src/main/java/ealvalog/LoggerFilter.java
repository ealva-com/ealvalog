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

/**
 * Used at top layer of logging to prevent unnecessary calls into lower layers, hence lots of unnecessary objects being created
 * <p>
 * Created by Eric A. Snell on 3/6/17.
 */
public interface LoggerFilter {
  /**
   * Will a log at this {@link LogLevel} result in an actual log statement
   *
   * @param logger    the logger that is making the log call
   * @param level the level to test, one of {@link LogLevel#TRACE}, {@link LogLevel#DEBUG}, {@link LogLevel#INFO}, {@link LogLevel#WARN},
   *              {@link LogLevel#ERROR}, {@link LogLevel#CRITICAL}
   *
   * @return true if a log statement will be produced at this level
   */
  FilterResult isLoggable(@NotNull Logger logger, @NotNull LogLevel level);

  /**
   * Will a log at this {@link LogLevel}, with the given (optional) {@link Marker} and (optional) {@link Throwable}, result in an actual log
   * statement
   *
   * @param logger    the logger that is making the log call
   * @param level     the level to test, one of {@link LogLevel#TRACE}, {@link LogLevel#DEBUG}, {@link LogLevel#INFO}, {@link
   *                  LogLevel#WARN}, {@link LogLevel#ERROR}, {@link LogLevel#CRITICAL}
   * @param marker    optional marker to test
   * @param throwable optional throwable to test
   *
   * @return true if a log statement will be produced at this level
   */
  FilterResult isLoggable(@NotNull Logger logger, @NotNull LogLevel level, @NotNull Marker marker, @NotNull Throwable throwable);
}
