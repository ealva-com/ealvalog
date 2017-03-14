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

package ealvalog.impl;

import ealvalog.filter.AlwaysNeutralFilter;
import ealvalog.FilterResult;
import ealvalog.LogLevel;
import ealvalog.Logger;
import ealvalog.LoggerFilter;
import ealvalog.NullMarker;
import ealvalog.util.NullThrowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Handler;

/**
 * Base class implementation of a {@link Handler}
 * <p>
 * Created by Eric A. Snell on 3/13/17.
 */
@SuppressWarnings("unused")
public abstract class BaseLoggerHandler extends Handler implements LoggerFilter {
  private @NotNull LoggerFilter filter;

  @SuppressWarnings("WeakerAccess")
  protected BaseLoggerHandler() {
    filter = AlwaysNeutralFilter.INSTANCE;
  }

  @SuppressWarnings("WeakerAccess")
  protected BaseLoggerHandler(final @NotNull LoggerFilter filter) {
    this.filter = filter;
  }

  @SuppressWarnings("WeakerAccess")
  public @NotNull LoggerFilter getLoggerFilter() {
    return filter;
  }

  public void setLoggerFilter(@Nullable final LoggerFilter filter) {
    this.filter = AlwaysNeutralFilter.nullToAlwaysNeutral(filter);
  }

  @Override public FilterResult isLoggable(@NotNull final Logger logger, @NotNull final LogLevel level) {
    return isLoggable(logger, level, NullMarker.INSTANCE, NullThrowable.INSTANCE);
  }
}
