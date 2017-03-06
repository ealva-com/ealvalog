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

import ealvalog.LogLevel;
import ealvalog.Marker;
import ealvalog.base.BaseLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * Implementation that uses {@link java.util.logging.Logger}
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public class JdkLogger extends BaseLogger {

  private java.util.logging.Logger jdkLogger;

  JdkLogger(final String name) {
    this(name, null);
  }

  JdkLogger(final String name, final Marker marker) {
    super(marker);
    jdkLogger = Logger.getLogger(name);
  }

  @NotNull @Override public String getName() {
    return jdkLogger.getName();
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level, @Nullable final Marker marker) {
    return jdkLogger.isLoggable(level.getLevel());
  }

  @Override
  protected void printLog(final @NotNull LogLevel level,
                          final @Nullable Marker marker,
                          final @Nullable Throwable throwable,
                          final @Nullable StackTraceElement callerLocation,
                          final @NotNull String msg,
                          final @NotNull Object[] formatArgs) {
    if (isLoggable(level, marker)) {
      ExtLogRecord logRecord = new ExtLogRecord(level, msg);
      if (callerLocation != null) {
        logRecord.setSourceClassName(callerLocation.getClassName());
        logRecord.setSourceMethodName(callerLocation.getMethodName());
        logRecord.setLineNumber(callerLocation.getLineNumber());
      }
      logRecord.setThrown(throwable);
      logRecord.setParameters(formatArgs);
      jdkLogger.log(logRecord);
    }
  }

  /** @return true if there is not a throwable and if the level is {@link LogLevel#CRITICAL} or {@link LogLevel#ERROR} */
  @Override protected boolean shouldIncludeLocation(final @NotNull LogLevel level,
                                                    final @Nullable Marker marker,
                                                    @Nullable final Throwable throwable) {
    return throwable == null && (level == LogLevel.CRITICAL || level == LogLevel.ERROR);
  }
}
