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

import ealvalog.filter.AlwaysNeutralFilter;
import ealvalog.FilterResult;
import ealvalog.LogLevel;
import ealvalog.LoggerFilter;
import ealvalog.Marker;
import ealvalog.NullMarker;
import ealvalog.core.Bridge;
import ealvalog.core.CoreLogger;
import ealvalog.util.LogUtil;
import ealvalog.util.NullThrowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ealvalog.FilterResult.ACCEPT;
import static ealvalog.FilterResult.DENY;

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

  @Override public boolean shouldLogToParent() {
    return jdkLogger.getUseParentHandlers();
  }

  @Override public void setLogToParent(final boolean logToParent) {
    jdkLogger.setUseParentHandlers(logToParent);
  }

  private boolean shouldIncludeLocation() {
    return includeLocation || (parent != null && parent.includeLocation);
  }

  @Override public FilterResult isLoggable(@NotNull final ealvalog.Logger logger, @NotNull final LogLevel level) {
    return isLoggable(logger, level, NullMarker.INSTANCE, NullThrowable.INSTANCE);
  }

  /**
   * {@inheritDoc}
   * <p>
   * If the level check passes and any contained filter does not deny, then accepted
   */
  @Override
  public FilterResult isLoggable(@NotNull final ealvalog.Logger logger,
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
    return ACCEPT;
  }

  @Override
  public void log(final @NotNull LogLevel level,
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
                                              getName(),
                                              shouldIncludeLocation() ? LogUtil.getCallerLocation(stackDepth + 1)
                                                                      : null,
                                              throwable,
                                              formatArgs);
    try {
      doLog(logRecord);
    } finally {
      ExtLogRecord.release(logRecord);
    }
  }

  // visible for test
  @SuppressWarnings("WeakerAccess")
  void doLog(final ExtLogRecord logRecord) {
    jdkLogger.log(logRecord);
  }

  @Override public String getName() {
    return jdkLogger.getName();
  }

  void addLoggerHandler(final BaseLoggerHandler loggerHandler) {
    jdkLogger.addHandler(loggerHandler);
  }

  void setLogLevel(final LogLevel logLevel) {
    jdkLogger.setLevel(logLevel.getJdkLevel());
  }
}
