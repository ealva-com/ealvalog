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

import ealvalog.Level;
import ealvalog.Marker;
import ealvalog.NullMarker;
import ealvalog.base.BaseLogger;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Implementation that uses {@link java.util.logging.Logger}
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public class JdkLogger extends BaseLogger {
  private java.util.logging.Logger jdkLogger;

  public JdkLogger(final String name) {
    jdkLogger = Logger.getLogger(name);
  }

  @NotNull @Override public String getName() {
    return jdkLogger.getName();
  }

  @Override public boolean isLoggable(@NotNull final Level level, @Nullable final Marker marker) {
    return jdkLogger.isLoggable(levelToJdkLevel(level));
  }

  private java.util.logging.Level levelToJdkLevel(final Level level) {
    switch (level) {
      case TRACE:
        return java.util.logging.Level.FINEST;
      case DEBUG:
        return java.util.logging.Level.FINE;
      case INFO:
        return java.util.logging.Level.INFO;
      case WARN:
        return java.util.logging.Level.WARNING;
      case ERROR:
        return java.util.logging.Level.SEVERE;
      case CRITICAL:
        return java.util.logging.Level.SEVERE;
    }
    throw new IllegalArgumentException("Level must be from TRACE to CRITICAL");
  }

  @Override
  protected void printLog(final @NotNull Level level,
                          final @Nullable Marker marker,
                          final @Nullable Throwable throwable,
                          final @Nullable StackTraceElement callerLocation,
                          final @NotNull LogMessageFormatter formatter,
                          final @NotNull String msg,
                          final @NotNull Object[] formatArgs) {
    if (isLoggable(level, marker)) {
      List<Object> parameters = new ArrayList<>(2);
      LogRecord logRecord = new LogRecord(levelToJdkLevel(level), formatter.format(msg, formatArgs).toString());
      parameters.add(NullMarker.nullToNullInstance(marker));
      if (callerLocation != null) {
        logRecord.setSourceClassName(callerLocation.getClassName());
        logRecord.setSourceMethodName(callerLocation.getMethodName());
        parameters.add(callerLocation.getLineNumber());
      }

      if (throwable != null) {
        logRecord.setThrown(throwable);
      }
      logRecord.setParameters(parameters.toArray());
      postRecordToLogger(logRecord);
    }
  }

  /** Visible for test */
  @SuppressWarnings("WeakerAccess")
  void postRecordToLogger(final LogRecord logRecord) {
    jdkLogger.log(logRecord);
  }

  /** @return true if there is not a throwable and if the level is {@link Level#CRITICAL} or {@link Level#ERROR} */
  @Override protected boolean shouldIncludeLocation(final @NotNull Level level, final @Nullable Marker marker, @Nullable final Throwable throwable) {
    return throwable == null && (level == Level.CRITICAL || level == Level.ERROR);
  }
}
