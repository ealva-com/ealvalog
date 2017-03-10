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

import ealvalog.LoggerFilter;
import ealvalog.Marker;
import ealvalog.core.CoreLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation that uses {@link java.util.logging.Logger}
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("WeakerAccess")
public class JdkLogger extends CoreLogger<JdkBridge> {
  private JdkLoggerConfiguration config;

  JdkLogger(final String name, final boolean includeLocation, @Nullable final Marker marker, final JdkLoggerConfiguration config) {
    super(name, config.getBridge(name), marker);
    setIncludeLocation(includeLocation);
    this.config = config;
  }

  protected @NotNull JdkBridge getBridge() {
    return super.getBridge();
  }

  void update(final @NotNull JdkLoggerConfiguration configuration) {
    this.config = configuration;
    setBridge(configuration.getBridge(getName()));
  }

  @Override public void setFilter(@NotNull final LoggerFilter filter) {
    config.setLoggerFilter(this, filter);
  }

}
