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

package com.ealva.ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * A no-op {@link com.ealva.ealvalog.LoggerFactory} instance - returns only {@link com.ealva.ealvalog.NullLogger#INSTANCE}
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public enum NullLoggerFactory implements com.ealva.ealvalog.LoggerFactory {
  INSTANCE;

  @NotNull public com.ealva.ealvalog.Logger get(@NotNull final String name) {
    return com.ealva.ealvalog.NullLogger.INSTANCE;
  }

  @NotNull @Override public com.ealva.ealvalog.Logger get(@NotNull final String name, final boolean includeLocation) {
    return com.ealva.ealvalog.NullLogger.INSTANCE;
  }

  @Override public @NotNull com.ealva.ealvalog.Logger get(@NotNull final String name, @NotNull final com.ealva.ealvalog.Marker marker) {
    return com.ealva.ealvalog.NullLogger.INSTANCE;
  }

  @NotNull @Override public com.ealva.ealvalog.Logger get(@NotNull final String name, @NotNull final com.ealva.ealvalog.Marker marker, final boolean includeLocation) {
    return com.ealva.ealvalog.NullLogger.INSTANCE;
  }
}