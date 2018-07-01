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

package com.ealva.ealvalog.core;

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Marker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.LogRecord;

/**
 * This logger delegates to a {@link com.ealva.ealvalog.core.Bridge} for {@link #isLoggable(LogLevel, Marker, Throwable)} and
 * {@link #printLog(LogLevel, Marker, Throwable, int, String, Object...)}
 * <p>
 * Created by Eric A. Snell on 3/7/17.
 */
public abstract class CoreLogger<T extends com.ealva.ealvalog.core.Bridge> extends BaseLogger {
  private volatile @NotNull T bridge;

  protected CoreLogger(final @NotNull String name, final @NotNull T bridge, @Nullable final Marker marker) {
    super(name, marker);
    this.bridge = bridge;
  }

  @SuppressWarnings("SameParameterValue")
  public abstract void setLogToParent(final boolean logToParent);

  @SuppressWarnings("unused")
  public abstract boolean getLogToParent();

  public abstract boolean shouldLogToParent();

  @Override
  protected void printLog(@NotNull final LogLevel level,
                          @Nullable final Marker marker,
                          @Nullable final Throwable throwable,
                          final int stackDepth,
                          @NotNull final String msg,
                          @NotNull final Object... formatArgs) {
    // isLoggable() should have already been called
    bridge.log(this, level, marker, throwable, stackDepth + 1, msg, formatArgs);
  }

  @Override public void logImmediate(@NotNull final LogRecord record) {
    bridge.log(record);
  }

  protected void setBridge(@NotNull final T bridge) {
    this.bridge = bridge;
  }

  protected @NotNull T getBridge() {
    return bridge;
  }

  public abstract void setFilter(@NotNull LoggerFilter filter);
}
