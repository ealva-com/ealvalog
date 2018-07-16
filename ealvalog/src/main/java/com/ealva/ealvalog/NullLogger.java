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

package com.ealva.ealvalog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.LogRecord;

/**
 * A no-op {@link com.ealva.ealvalog.Logger} implementation
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public final class NullLogger implements com.ealva.ealvalog.Logger {
  public static final com.ealva.ealvalog.Logger INSTANCE = new NullLogger();

  private NullLogger() {}

  @NotNull public String getName() {
    return "";
  }

  @SuppressWarnings("ConstantConditions")
  @Nullable public com.ealva.ealvalog.Marker getMarker() {
    return com.ealva.ealvalog.NullMarker.INSTANCE;
  }

  public void setMarker(@Nullable final com.ealva.ealvalog.Marker marker) {}

  @SuppressWarnings("ConstantConditions")
  @Nullable @Override public com.ealva.ealvalog.LogLevel getLogLevel() {
    return com.ealva.ealvalog.LogLevel.NONE;
  }

  @Override public void setLogLevel(@Nullable final com.ealva.ealvalog.LogLevel logLevel) {

  }

  @NotNull @Override public com.ealva.ealvalog.LogLevel getEffectiveLogLevel() {
    return com.ealva.ealvalog.LogLevel.NONE;
  }

  @Override public void setIncludeLocation(final boolean includeLocation) {

  }

  @Override public boolean getIncludeLocation() {
    return false;
  }

  public boolean isLoggable(@NotNull final com.ealva.ealvalog.LogLevel level) {
    return false;
  }

  @Override public boolean isLoggable(@NotNull final com.ealva.ealvalog.LogLevel level, @Nullable final com.ealva.ealvalog.Marker marker, @Nullable final Throwable throwable) {
    return false;
  }

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level, @NotNull final String msg) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level, @NotNull final com.ealva.ealvalog.Marker marker, @NotNull final String msg) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level, @NotNull final Throwable throwable, @NotNull final String msg) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level, @NotNull final com.ealva.ealvalog.Marker marker, @NotNull final Throwable throwable, @NotNull final String msg) {
  }

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level,
                  @NotNull final com.ealva.ealvalog.Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level,
                  @NotNull final com.ealva.ealvalog.Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  final @NotNull Object[] formatArgs) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level, @NotNull final String format, @NotNull final Object arg1) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level, @NotNull final String format, @NotNull final Object arg1, @NotNull final Object arg2) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3,
                  @NotNull final Object arg4) {}

  public void log(@NotNull final com.ealva.ealvalog.LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3,
                  @NotNull final Object arg4,
                  final @NotNull Object[] remaining) {}

  @Override public void caught(@NotNull final com.ealva.ealvalog.LogLevel level, @NotNull final Throwable throwable) {}

  @Override public Throwable throwing(@NotNull final com.ealva.ealvalog.LogLevel level, @NotNull final Throwable throwable) {
    return throwable;
  }


  @Override
  public void logImmediate(@NotNull final com.ealva.ealvalog.LogLevel level,
                           @Nullable final com.ealva.ealvalog.Marker marker,
                           @Nullable final Throwable throwable,
                           final int stackDepth,
                           @NotNull final String msg,
                           final @NotNull Object[] formatArgs) {}

  @Override
  public void logImmediate(@NotNull final com.ealva.ealvalog.LogLevel level,
                           @Nullable final Throwable throwable,
                           final int stackDepth,
                           @NotNull final String msg,
                           final @NotNull Object[] formatArgs) {}

  @Override public void logImmediate(@NotNull final LogRecord record) {}

}
