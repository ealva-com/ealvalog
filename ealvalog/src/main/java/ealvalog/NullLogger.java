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
 * A no-op {@link Logger} implementation
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public enum NullLogger implements Logger {
  INSTANCE;

  @NotNull public String getName() {
    return "";
  }

  @Nullable public Marker getMarker() {
    return null;
  }

  public void setMarker(@Nullable final Marker marker) {}

  public boolean isLoggable(@NotNull final LogLevel level) {
    return false;
  }

  public boolean isLoggable(@NotNull final LogLevel level, @Nullable final Marker marker) {
    return false;
  }

  public void log(@NotNull final LogLevel level, @NotNull final String msg) {}

  public void log(@NotNull final LogLevel level, @NotNull final Marker marker, @NotNull final String msg) {}

  public void log(@NotNull final LogLevel level, @NotNull final Throwable throwable, @NotNull final String msg) {}

  public void log(@NotNull final LogLevel level, @NotNull final Marker marker, @NotNull final Throwable throwable, @NotNull final String msg) {
  }

  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {}

  public void log(@NotNull final LogLevel level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {}

  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  final @NotNull Object[] formatArgs) {}

  public void log(@NotNull final LogLevel level, @NotNull final String format, @NotNull final Object arg1) {}

  public void log(@NotNull final LogLevel level, @NotNull final String format, @NotNull final Object arg1, @NotNull final Object arg2) {}

  public void log(@NotNull final LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3) {}

  public void log(@NotNull final LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3,
                  @NotNull final Object arg4) {}

  public void log(@NotNull final LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3,
                  @NotNull final Object arg4,
                  final @NotNull Object[] remaining) {}

  @Override
  public void logImmediate(@NotNull final LogLevel level,
                           @Nullable final Marker marker,
                           @Nullable final Throwable throwable,
                           final int stackDepth,
                           @NotNull final String msg,
                           final @NotNull Object[] formatArgs) {}

  @Override
  public void logImmediate(@NotNull final LogLevel level,
                           @Nullable final Throwable throwable,
                           final int stackDepth,
                           @NotNull final String msg,
                           final @NotNull Object[] formatArgs) {}

}
