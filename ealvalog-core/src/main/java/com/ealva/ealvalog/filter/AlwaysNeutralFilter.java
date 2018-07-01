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

package com.ealva.ealvalog.filter;

import com.ealva.ealvalog.FilterResult;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.ealva.ealvalog.FilterResult.NEUTRAL;

/**
 * A filter that always responds true. Helps avoid null.
 * <p>
 * Created by Eric A. Snell on 3/8/17.
 */
public enum AlwaysNeutralFilter implements LoggerFilter {
  INSTANCE;

  /**
   * If {@code filter} is null, returns an AlwaysYesFilter, else returns {@code filter}
   *
   * @param filter optional filter
   *
   * @return the filter parameter if not null, else {@link AlwaysNeutralFilter#INSTANCE}
   */
  public static @NotNull LoggerFilter nullToAlwaysNeutral(final @Nullable LoggerFilter filter) {
    return filter == null ? INSTANCE : filter;
  }


  @Override public FilterResult isLoggable(@NotNull final Logger logger, @NotNull final LogLevel level) {
    return NEUTRAL;
  }

  @Override
  public FilterResult isLoggable(@NotNull final Logger logger,
                                 @NotNull final LogLevel level,
                                 @NotNull final Marker marker,
                                 @NotNull final Throwable throwable) {
    return NEUTRAL;
  }
}
