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

package com.ealva.ealvalog.java;

import com.ealva.ealvalog.LogEntry;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.util.LogUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Created by Eric A. Snell on 8/13/18.
 */
public class JLoggerImpl implements JLogger {
  private static final int STACK_DEPTH = 1;

  private @NotNull Logger realLogger;

  public JLoggerImpl(@NotNull Logger realLogger) {
    this.realLogger = realLogger;
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level) {
    return realLogger.isLoggable(level, null, null);
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level, @NotNull final Marker marker) {
    return realLogger.isLoggable(level, marker, null);
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level, @NotNull final Throwable ex) {
    return realLogger.isLoggable(level, null, ex);
  }

  @Override public void log(@NotNull final LogLevel level, @NotNull final String msg) {
    if (isLoggable(level, realLogger.getMarker(), null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.append(msg);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final String msg) {
    if (isLoggable(level, marker, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    marker,
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, marker, null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.append(msg);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Throwable throwable,
                  @NotNull final String msg) {
    if (isLoggable(level, realLogger.getMarker(), throwable)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    throwable)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), throwable)) {
          record.addLocation(STACK_DEPTH);
        }
        record.append(msg);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String msg) {
    if (isLoggable(level, marker, throwable)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    marker,
                                                    throwable)) {
        if (realLogger.shouldIncludeLocation(level, marker, throwable)) {
          record.addLocation(STACK_DEPTH);
        }
        record.append(msg);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    marker,
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, marker, null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.log(format, formatArgs);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, null, throwable)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    throwable)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), throwable)) {
          record.addLocation(STACK_DEPTH);
        }
        record.log(format, formatArgs);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker, throwable)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    marker,
                                                    throwable)) {
        if (realLogger.shouldIncludeLocation(level, marker, throwable)) {
          record.addLocation(STACK_DEPTH);
        }
        record.log(format, formatArgs);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1) {
    if (isLoggable(level, null, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.log(format, arg1);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2) {
    if (isLoggable(level, null, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.log(format, arg1, arg2);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3) {
    if (isLoggable(level, null, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.log(format, arg1, arg2, arg3);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3,
                  @NotNull final Object arg4) {
    if (isLoggable(level, null, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.log(format, arg1, arg2, arg3, arg4);
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final String format,
                  @NotNull final Object arg1,
                  @NotNull final Object arg2,
                  @NotNull final Object arg3,
                  @NotNull final Object arg4,
                  @NotNull final Object... remaining) {
    if (isLoggable(level, null, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        // JLoggerImpl and ExtLogRecord are Java to avoid Kotlin's SpreadBuilder
        // used when passing vararg to vararg and the spread operator is required. Instead
        // we'll use our own combineArgs() so the spread functionality is isolated here and
        // not repeated multiple times in Kotlin code
        record.log(format, LogUtil.combineArgs(remaining, arg1, arg2, arg3, arg4));
        logImmediate(record);
      }
    }
  }

  public void log(@NotNull final LogLevel level, @NotNull final Supplier<?> supplier) {
    if (isLoggable(level, null, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.append(supplier.get().toString());
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Supplier<?> supplier) {
    if (isLoggable(level, marker, null)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    marker,
                                                    null)) {
        if (realLogger.shouldIncludeLocation(level, marker, null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.append(supplier.get().toString());
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Throwable throwable,
                  @NotNull final Supplier<?> supplier) {
    if (isLoggable(level, null, throwable)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    throwable)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), throwable)) {
          record.addLocation(STACK_DEPTH);
        }
        record.append(supplier.get().toString());
        logImmediate(record);
      }
    }
  }

  @Override
  public void log(@NotNull final LogLevel level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final Supplier<?> supplier) {
    if (isLoggable(level, marker, throwable)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    marker,
                                                    throwable)) {
        if (realLogger.shouldIncludeLocation(level, marker, throwable)) {
          record.addLocation(STACK_DEPTH);
        }
        record.append(supplier.get().toString());
        logImmediate(record);
      }
    }
  }

  @Override public void caught(@NotNull final LogLevel level, @NotNull final Throwable throwable) {
    if (isLoggable(level, null, throwable)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    throwable)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.format("Caught: %s", throwable.getMessage());
        logImmediate(record);
      }
    }
  }

  @NotNull @Override
  public <T extends Throwable> T throwing(@NotNull final LogLevel level,
                                          @NotNull final T throwable) {
    if (isLoggable(level, null, throwable)) {
      try (LogEntry record = realLogger.getLogEntry(level,
                                                    realLogger.getMarker(),
                                                    throwable)) {
        if (realLogger.shouldIncludeLocation(level, realLogger.getMarker(), null)) {
          record.addLocation(STACK_DEPTH);
        }
        record.format("Throwing: %s", throwable.getMessage());
        logImmediate(record);
      }
    }
    return throwable;
  }

  @NotNull @Override public String getName() {
    return realLogger.getName();
  }

  @Nullable @Override public Marker getMarker() {
    return realLogger.getMarker();
  }

  @Override public void setMarker(@Nullable final Marker marker) {
    realLogger.setMarker(marker == null ? NullMarker.INSTANCE : marker);
  }

  @Nullable @Override public LogLevel getLogLevel() {
    return realLogger.getLogLevel();
  }

  @Override public void setLogLevel(@Nullable final LogLevel logLevel) {
    realLogger.setLogLevel(logLevel);
  }

  @NotNull @Override public LogLevel getEffectiveLogLevel() {
    return realLogger.getEffectiveLogLevel();
  }

  @Override public boolean getIncludeLocation() {
    return realLogger.getIncludeLocation();
  }

  @Override
  public boolean shouldIncludeLocation(@NotNull final LogLevel logLevel,
                                       @Nullable final Marker marker,
                                       @Nullable final Throwable throwable) {
    return realLogger.shouldIncludeLocation(logLevel, marker, throwable);
  }

  @Override public void setIncludeLocation(final boolean includeLocation) {
    realLogger.setIncludeLocation(includeLocation);
  }

  @Override
  public boolean isLoggable(@NotNull final LogLevel level,
                            @Nullable final Marker marker,
                            @Nullable final Throwable throwable) {
    return realLogger.isLoggable(level, marker, throwable);
  }

  @Override public void logImmediate(@NotNull final LogEntry entry) {
    realLogger.logImmediate(entry);
  }

  @NotNull @Override
  public LogEntry getLogEntry(@NotNull final LogLevel logLevel,
                              @Nullable final Marker marker,
                              @Nullable final Throwable throwable) {
    return realLogger.getLogEntry(logLevel, marker, throwable);
  }

  @NotNull @Override public LoggerFilter getFilter() {
    return realLogger.getFilter();
  }

  @Override public void setFilter(@NotNull final LoggerFilter loggerFilter) {
    realLogger.setFilter(loggerFilter);
  }
}
