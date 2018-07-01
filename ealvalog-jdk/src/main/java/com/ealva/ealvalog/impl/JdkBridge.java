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
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.core.Bridge;
import com.ealva.ealvalog.core.CoreLogger;
import com.ealva.ealvalog.core.ExtLogRecord;
import com.ealva.ealvalog.filter.AlwaysNeutralFilter;
import com.ealva.ealvalog.util.LogUtil;
import com.ealva.ealvalog.util.NullThrowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.ealva.ealvalog.FilterResult.DENY;
import static com.ealva.ealvalog.FilterResult.NEUTRAL;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Bridge the {@link CoreLogger} to the underlying java.util.logging.Logger
 * <p>
 * Created by Eric A. Snell on 3/7/17.
 */
public class JdkBridge implements Bridge {
  private volatile JdkBridge parent;  // root bridge will have a null parent
  private final @NotNull java.util.logging.Logger jdkLogger;
  private volatile LoggerFilter filter;
  private boolean includeLocation;

  JdkBridge(final @NotNull String name) {
    parent = null;
    jdkLogger = Logger.getLogger(name);
    filter = AlwaysNeutralFilter.INSTANCE;
    includeLocation = false;
  }

  JdkBridge(final String loggerName,
            final @Nullable LoggerFilter filter,
            final @Nullable BaseLoggerHandler handler,
            final @Nullable LogLevel level) {
    this(loggerName);
    setFilter(filter);
    if (handler != null) {
      addLoggerHandler(handler);
    }
    if (level != null) {
      setLogLevel(level);
    }
  }

  @Nullable JdkBridge getParent() {
    return parent;
  }

  void setParent(final @Nullable JdkBridge parent) {
    this.parent = parent;
  }

  @Nullable LoggerFilter getFilter() {
    return filter;
  }

  void setFilter(final @Nullable LoggerFilter filter) {
    this.filter = AlwaysNeutralFilter.nullToAlwaysNeutral(filter);
  }

  /** @return the include location flag */
  @Override public boolean getIncludeLocation() {
    return includeLocation;
  }

  /** Set the include location flag */
  @Override public void setIncludeLocation(final boolean includeLocation) {
    this.includeLocation = includeLocation;
  }

  private boolean shouldIncludeLocation() {
    return includeLocation || (parent != null && parent.shouldIncludeLocation());
  }

  @Override public boolean shouldLogToParent(final com.ealva.ealvalog.Logger logger) {
    return !bridgeIsLoggerPeer(logger) || jdkLogger.getUseParentHandlers();
  }

  @Override public void setLogToParent(final boolean logToParent) {
    jdkLogger.setUseParentHandlers(logToParent);
  }

  @Override public FilterResult isLoggable(@NotNull final com.ealva.ealvalog.Logger logger, @NotNull final LogLevel level) {
    return isLoggable(logger, level, NullMarker.INSTANCE, NullThrowable.INSTANCE);
  }

  /**
   * {@inheritDoc}
   * <p>
   * If the level check passes and any contained filter does not deny, then accepted
   */
  @Override
  public FilterResult isLoggable(@NotNull final com.ealva.ealvalog.Logger logger,
                                 @NotNull final LogLevel level,
                                 @NotNull final Marker marker,
                                 @NotNull final Throwable throwable) {
    if (!jdkLogger.isLoggable(level.getJdkLevel())) {
      return DENY;
    }
    final FilterResult filterResult = filter.isLoggable(logger, level, marker, throwable);
    if (filterResult == DENY) {
      return DENY;
    }
    return NEUTRAL;
  }

  @Override
  public void log(final @NotNull com.ealva.ealvalog.Logger logger,
                  final @NotNull LogLevel level,
                  final @Nullable Marker marker,
                  final @Nullable Throwable throwable,
                  final int stackDepth,
                  final @NotNull String msg,
                  final @NotNull Object... formatArgs) {
    // ENSURE the record obtained is released!
    //
    // We're not using try with resources here due to warnings about early Android versions.
    ExtLogRecord logRecord = ExtLogRecord.get(level,
                                              msg,
                                              logger.getName(),
                                              shouldIncludeLocation() ? LogUtil.getCallerLocation(stackDepth + 1)
                                                                      : null,
                                              throwable,
                                              formatArgs);
    try {
      log(logRecord);
    } finally {
      ExtLogRecord.release(logRecord);
    }
  }

  @Override
  public void log(@NotNull final LogRecord logRecord) {
    jdkLogger.log(logRecord);
  }

  @Override public String getName() {
    return jdkLogger.getName();
  }

  @Nullable @Override public LogLevel getLevelForLogger(final com.ealva.ealvalog.Logger logger) {
    if (bridgeIsLoggerPeer(logger)) {
      return LogLevel.fromLevel(jdkLogger.getLevel());
    }
    return null;
  }

  @Override public boolean bridgeIsLoggerPeer(final com.ealva.ealvalog.Logger logger) {
    return getName().equals(logger.getName());
  }

  @Override public LogLevel getLogLevel() {
    return LogLevel.fromLevel(jdkLogger.getLevel());
  }

  void addLoggerHandler(final @NotNull BaseLoggerHandler loggerHandler) {
    jdkLogger.addHandler(loggerHandler);
  }

  void setLogLevel(final LogLevel logLevel) {
    jdkLogger.setLevel(logLevel.getJdkLevel());
  }

  boolean getLogToParent() {
    return jdkLogger.getUseParentHandlers();
  }

  void setToDefault() {
    jdkLogger.setUseParentHandlers(true);
  }
}
