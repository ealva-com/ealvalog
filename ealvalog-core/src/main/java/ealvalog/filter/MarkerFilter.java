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

package ealvalog.filter;

import ealvalog.FilterResult;
import ealvalog.LogLevel;
import ealvalog.Logger;
import ealvalog.Marker;
import ealvalog.NullMarker;
import ealvalog.util.NullThrowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Filter which checks Marker equality
 *
 * Created by Eric A. Snell on 3/13/17.
 */
public class MarkerFilter extends BaseFilter {
  @NotNull private final Marker marker;

  protected MarkerFilter() {
    marker = NullMarker.INSTANCE;
  }

  protected MarkerFilter(final @NotNull Marker marker, @NotNull final FilterResult whenMatched, @NotNull final FilterResult whenDiffer) {
    super(whenMatched, whenDiffer);
    this.marker = marker;
  }

  @Override public FilterResult isLoggable(@NotNull final Logger logger, @NotNull final LogLevel level) {
    return isLoggable(logger, level, NullMarker.INSTANCE, NullThrowable.INSTANCE);
  }

  @Override
  public FilterResult isLoggable(@NotNull final Logger logger,
                                 @NotNull final LogLevel level,
                                 @Nullable final Marker marker,
                                 @Nullable final Throwable throwable) {
    return result(this.marker.isOrContains(NullMarker.nullToNullInstance(marker)));
  }

}
