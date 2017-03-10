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
 * A filter that always responds true. Helps avoid null.
 *
 * Created by Eric A. Snell on 3/8/17.
 */
public enum AlwaysYesFilter implements LoggerFilter {
  INSTANCE;

  /**
   * If {@code filter} is null, returns an AlwaysYesFilter, else returns {@code filter}
   *
   * @param filter optional filter
   *
   * @return the filter parameter if not null, else {@link AlwaysYesFilter#INSTANCE}
   */
  public static @NotNull LoggerFilter nullToAlwaysYes(final @Nullable LoggerFilter filter) {
    return filter == null ? INSTANCE : filter;
  }

  /**
   * {@inheritDoc}

   * @return always true
   */
  @Override public boolean isLoggable(@NotNull final LogLevel level) {
    return true;
  }

  /**
   * {@inheritDoc}
   * @return always returns true
   */
  @Override public boolean isLoggable(final @NotNull LogLevel level, final @Nullable Marker marker, final @Nullable Throwable throwable) {
    return true;
  }

}
