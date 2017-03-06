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

package ealvalog.base;

import ealvalog.LogLevel;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Log to multiple Logger implementations
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
public class CompositeLogger extends BaseLogger {
  private final String name;
  private final List<BaseLogger> loggerList;

  public CompositeLogger(final String name, List<BaseLogger> loggers) {
    this.name = name;
    loggerList = new ArrayList<>(loggers);
  }

  public CompositeLogger(final String name, @NotNull BaseLogger... loggers) {
    this.name = name;
    loggerList = Arrays.asList(loggers);
  }

  @Override public @NotNull String getName() {
    return name;
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level, @Nullable final Marker marker) {
    for (int i = 0, size = loggerList.size(); i < size; i++) {
      if (loggerList.get(i).isLoggable(level, marker)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void printLog(final @NotNull LogLevel level,
                          final @Nullable Marker marker,
                          final @Nullable Throwable throwable,
                          final @Nullable StackTraceElement callerLocation,
                          final @NotNull String msg,
                          final @NotNull Object[] formatArgs) {
    for (int i = 0, size = loggerList.size(); i < size; i++) {
      final BaseLogger baseLogger = loggerList.get(i);
      if (baseLogger.isLoggable(level, marker)) {
        baseLogger.printLog(level, marker, throwable, callerLocation, msg, formatArgs);
      }
    }
  }

  @Override protected boolean shouldIncludeLocation(@NotNull final LogLevel level, @Nullable final Marker marker, @Nullable final Throwable throwable) {
    for (int i = 0, size = loggerList.size(); i < size; i++) {
      if (loggerList.get(i).shouldIncludeLocation(level, marker, throwable)) {
        return true;
      }
    }
    return false;
  }
}
