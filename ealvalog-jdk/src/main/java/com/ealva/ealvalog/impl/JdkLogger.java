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
import com.ealva.ealvalog.core.CoreLogger;
import com.ealva.ealvalog.util.NullThrowable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.ealva.ealvalog.LogLevel.NONE;

/**
 * Implementation that uses {@link java.util.logging.Logger}
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("WeakerAccess")
public class JdkLogger extends CoreLogger<JdkBridge> {
  private JdkLoggerConfiguration config;

  JdkLogger(final String name, @Nullable final Marker marker, final JdkLoggerConfiguration config) {
    super(name, config.getBridge(name), marker);
    this.config = config;
  }

  @Nullable @Override public LogLevel getLogLevel() {
    return getBridge().getLevelForLogger(this);
  }

  @Override public void setLogLevel(@Nullable final LogLevel logLevel) {
    config.setLogLevel(this, logLevel == null ? NONE : logLevel);
  }

  @NotNull @Override public LogLevel getEffectLogLevel() {
    return getBridge().getLogLevel();
  }

  void update(final @NotNull JdkLoggerConfiguration configuration) {
    this.config = configuration;
    setBridge(configuration.getBridge(getName()));
  }  @Override public void setIncludeLocation(final boolean includeLocation) {
    config.setIncludeLocation(this, includeLocation);
  }

  public void addHandler(final @NotNull BaseLoggerHandler handler) {
    config.addLoggerHandler(this, handler);
  }  @Override public boolean getIncludeLocation() {
    return getBridge().getIncludeLocation();
  }

  @Override
  public boolean isLoggable(final @NotNull LogLevel level,
                            @Nullable final Marker marker,
                            @Nullable final Throwable throwable) {
    return getBridge().isLoggable(this,
                                  level,
                                  NullMarker.nullToNullInstance(marker),
                                  NullThrowable.nullToNullInstance(throwable)) != FilterResult.DENY;
  }

  @Override public void setLogToParent(final boolean logToParent) {
    config.setLogToParent(this, logToParent);
  }

  @Override public boolean getLogToParent() {
    return getBridge().getLogToParent();
  }

  @Override public boolean shouldLogToParent() {
    return getBridge().shouldLogToParent(this);
  }

  protected @NotNull JdkBridge getBridge() {
    return super.getBridge();
  }



  @Override public void setFilter(@NotNull final LoggerFilter filter) {
    config.setLoggerFilter(this, filter);
  }



}
