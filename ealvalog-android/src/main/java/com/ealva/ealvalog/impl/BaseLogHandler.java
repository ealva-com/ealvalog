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

package com.ealva.ealvalog.impl;

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.core.ExtRecordFormatter;
import com.ealva.ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.os.Build;
import android.util.Log;

import java.util.Locale;
import java.util.logging.LogRecord;

/**
 * Base handler prepares a default style message including log call site information
 *
 * Created by Eric A. Snell on 3/3/17.
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseLogHandler implements LogHandler {
  private ExtRecordFormatter formatter = new ExtRecordFormatter(ExtRecordFormatter.TYPICAL_ANDROID_FORMAT);

  protected Locale getLocale() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return Locale.getDefault(Locale.Category.FORMAT);
    } else {
      return Locale.getDefault();
    }
  }

  @Override public void prepareLog(@NotNull final LogRecord record) {
    log(
        Levels.toAndroidLevel(LogLevel.fromLevel(record.getLevel())),
        record.getLoggerName(),
        formatter.format(record),
        record.getThrown()
    );
  }

  @Override
  public void prepareLog(final String tag,
                         final int level,
                         @Nullable final Marker marker,
                         @Nullable final Throwable throwable,
                         @Nullable final StackTraceElement callerLocation,
                         @NotNull final LogMessageFormatter formatter,
                         @NotNull final String msg,
                         @NotNull final Object... formatArgs) {
    if (callerLocation != null) {
      formatter.append('(')
               .append(callerLocation.getMethodName())
               .append(':')
               .append(callerLocation.getLineNumber())
               .append(") ");
    }
    final String logMessage = formatter.append(getLocale(), msg, formatArgs)
                                       .toString();
    log(level, tag, logMessage, throwable);
  }

  protected void log(final int level,
                     final String tag,
                     final String logMessage,
                     final @Nullable Throwable throwable) {
    switch (level) {
      case Log.VERBOSE:
        Log.v(tag, logMessage, throwable);
        break;
      case Log.DEBUG:
        Log.d(tag, logMessage, throwable);
        break;
      case Log.INFO:
        Log.i(tag, logMessage, throwable);
        break;
      case Log.WARN:
        Log.w(tag, logMessage, throwable);
        break;
      case Log.ERROR:
        Log.e(tag, logMessage, throwable);
        break;
      case Log.ASSERT:
        Log.wtf(tag, logMessage, throwable);
        break;
      default:
        Log.wtf(tag, "LOGGER CONFIG ERROR (" + level + ") " + logMessage, throwable);
        break;
    }
  }

}
