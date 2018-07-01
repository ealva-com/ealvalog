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

import com.ealva.ealvalog.FilterResult;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.core.ExtRecordFormatter;
import com.ealva.ealvalog.filter.AlwaysNeutralFilter;

import com.ealva.ealvalog.util.Levels;
import org.jetbrains.annotations.NotNull;

import static com.ealva.ealvalog.FilterResult.DENY;
import static com.ealva.ealvalog.util.LogUtil.tagFromName;

import android.util.Log;

import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Handler for the jdk facade implementation which logs to the Android Log
 * <p>
 * Created by Eric A. Snell on 3/10/17.
 */
public class AndroidLoggerHandler extends BaseLoggerHandler {
  public static final class Builder {
    private @NotNull String formatterPattern;
    private boolean formatterLogErrors;
    private @NotNull LoggerFilter filter;
    private @NotNull ErrorManager errorManager;

    Builder() {
      formatterPattern = ExtRecordFormatter.TYPICAL_ANDROID_FORMAT;
      formatterLogErrors = true;
      filter = AlwaysNeutralFilter.INSTANCE;
      errorManager = new ErrorManager();
    }

    public Builder extRecordFormatterPattern(final @NotNull String pattern) {
      this.formatterPattern = pattern;
      return this;
    }

    public Builder formatterLogErrors(final boolean logErrors) {
      this.formatterLogErrors = logErrors;
      return this;
    }

    @SuppressWarnings("WeakerAccess")
    public Builder filter(final @NotNull LoggerFilter filter) {
      this.filter = filter;
      return this;
    }

    public Builder errorManager(final @NotNull ErrorManager errorManager) {
      this.errorManager = errorManager;
      return this;
    }

    @SuppressWarnings("WeakerAccess")
    public AndroidLoggerHandler build() throws IllegalStateException {
      return new AndroidLoggerHandler(new ExtRecordFormatter(formatterPattern, formatterLogErrors),
                                      filter,
                                      errorManager);
    }
  }

  @SuppressWarnings("WeakerAccess")
  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("WeakerAccess")
  protected AndroidLoggerHandler(final @NotNull Formatter formatter,
                                 final @NotNull LoggerFilter filter,
                                 final @NotNull ErrorManager errorManager) {
    super(filter);
    setFormatter(formatter);
    setErrorManager(errorManager);
  }

  @Override
  public FilterResult isLoggable(@NotNull final Logger logger,
                                 @NotNull final LogLevel level,
                                 @NotNull final Marker marker,
                                 @NotNull final Throwable throwable) {
    if (isLoggable(tagFromName(logger.getName()), Levels.toAndroidLevel(level))) {
      return getLoggerFilter().isLoggable(logger, level, marker, throwable);
    }
    return DENY;
  }

  @SuppressWarnings("WeakerAccess")
  protected boolean isLoggable(final @NotNull String tag, final int androidLevel) {
    return Log.isLoggable(tag, androidLevel);
  }



  @Override public void publish(final LogRecord record) {
    final int androidLevel = Levels.toAndroidLevel(LogLevel.fromLevel(record.getLevel()));
    final String tag = tagFromName(record.getLoggerName());
    if (isLoggable(tag, androidLevel)) {
      final String msg = getFormatter().format(record);
      switch (androidLevel) {
        case Log.VERBOSE:
          Log.v(tag, msg, record.getThrown());
          break;
        case Log.DEBUG:
          Log.d(tag, msg, record.getThrown());
          break;
        case Log.INFO:
          Log.i(tag, msg, record.getThrown());
          break;
        case Log.WARN:
          Log.w(tag, msg, record.getThrown());
          break;
        case Log.ERROR:
          Log.e(tag, msg, record.getThrown());
          break;
        case Log.ASSERT:
          Log.wtf(tag, msg, record.getThrown());
          break;
      }
    }
  }

  @Override public void flush() {}

  @Override public void close() {}

}
