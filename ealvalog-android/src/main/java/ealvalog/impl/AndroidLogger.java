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
import ealvalog.base.LogUtil;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.util.Log;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logger implementation for Android
 * <p>
 * Created by Eric A. Snell on 3/3/17.
 */
@SuppressWarnings("WeakerAccess")
public class AndroidLogger extends BaseLogger {
  private static final ThreadLocal<LogMessageFormatter> threadLocalFormatter =
      new ThreadLocal<LogMessageFormatter>() {
        @Override
        protected LogMessageFormatter initialValue() {
          return new LogMessageFormatter();
        }

        @Override
        public LogMessageFormatter get() {
          LogMessageFormatter lmf = super.get();
          lmf.reset();
          return lmf;
        }
      };

  private static final int MAX_TAG_LENGTH = 23;
  private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

  private static AtomicReference<LogHandler> logHandler = new AtomicReference<>();

  public static void setHandler(@NotNull final LogHandler handler) {
    logHandler.set(handler);
  }

  public static LogHandler getHandler() {
    return logHandler.get();
  }

  public static void removeHandler() {
    logHandler.set(NullLogHandler.INSTANCE);
  }

  private final String tag;
  private boolean includeLocation;

  AndroidLogger(final String name, final boolean includeLocation, final @Nullable Marker marker) {
    super(name, marker);
    tag = tagFromName(name);
    this.includeLocation = includeLocation;
  }

  private String tagFromName(final String name) {
    String tag = name;
    Matcher m = ANONYMOUS_CLASS.matcher(tag);
    if (m.find()) {
      tag = m.replaceAll("");
    }
    tag = tag.substring(tag.lastIndexOf('.') + 1);
    return tag.length() > MAX_TAG_LENGTH ? tag.substring(0, MAX_TAG_LENGTH) : tag;
  }

  @Override public boolean isLoggable(final @NotNull LogLevel level, final @Nullable Marker marker, final @Nullable Throwable throwable) {
    return logHandler.get().isLoggable(tag, levelToAndroidLevel(level));
  }

  @Override
  protected void printLog(final @NotNull LogLevel level,
                          final @Nullable Marker marker,
                          final @Nullable Throwable throwable,
                          final int stackDepth,
                          final @NotNull String msg,
                          final @NotNull Object... formatArgs) {
    final int androidLevel = levelToAndroidLevel(level);
    logHandler.get().prepareLog(tag,
                                androidLevel,
                                marker,
                                throwable,
                                getLogSiteInfo(androidLevel, marker, throwable, stackDepth + 1),
                                threadLocalFormatter.get(),
                                msg,
                                formatArgs);
  }

  private StackTraceElement getLogSiteInfo(final int androidLevel, final Marker marker, final Throwable throwable, final int stackDepth) {
    if (includeLocation || logHandler.get().shouldIncludeLocation(tag, androidLevel, marker, throwable)) {
      return LogUtil.getCallerLocation(stackDepth + 1);
    }
    return null;
  }

  protected int levelToAndroidLevel(@NotNull final LogLevel level) {
    switch (level) {
      case TRACE:
        return Log.VERBOSE;
      case DEBUG:
        return Log.DEBUG;
      case INFO:
        return Log.INFO;
      case WARN:
        return Log.WARN;
      case ERROR:
        return Log.ERROR;
      case CRITICAL:
        return Log.ASSERT;
      default:
        throw new IllegalArgumentException("Illegal Level to map to Android");
    }
  }

  @Override public void setIncludeLocation(final boolean includeLocation) {
    this.includeLocation = includeLocation;
  }

  @Override public boolean getIncludeLocation() {
    return includeLocation;
  }
}
