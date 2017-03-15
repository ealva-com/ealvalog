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

import ealvalog.FilterResult;
import ealvalog.LogLevel;
import ealvalog.Logger;
import ealvalog.LoggerFilter;
import ealvalog.Marker;
import ealvalog.util.Levels;
import org.jetbrains.annotations.NotNull;

import static ealvalog.FilterResult.DENY;
import static ealvalog.util.LogUtil.tagFromName;

import android.util.Log;

import java.util.logging.LogRecord;

/**
 * Handler for the jdk facade implementation which logs to the Android Log
 * <p>
 * Created by Eric A. Snell on 3/10/17.
 */
public class AndroidLoggerHandler extends BaseLoggerHandler {

  @SuppressWarnings("WeakerAccess")
  public AndroidLoggerHandler() {}

  @SuppressWarnings("WeakerAccess")
  public AndroidLoggerHandler(@NotNull final LoggerFilter filter) {
    super(filter);
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
