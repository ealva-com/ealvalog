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

import ealvalog.Marker;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * No-op LogHandler does nothing. Can be used to turn off all logging
 * <p>
 * Created by Eric A. Snell on 3/3/17.
 */
@SuppressWarnings("WeakerAccess")
public final class NullLogHandler implements LogHandler {
  public static final LogHandler INSTANCE = new NullLogHandler();

  private NullLogHandler() {}

  @Override public boolean isLoggable(@NotNull final String tag, final int level) {
    return false;
  }

  @Override
  public boolean shouldIncludeLocation(@NotNull final String tag,
                                       final int level,
                                       @Nullable final Marker marker,
                                       @Nullable final Throwable throwable) {
    return false;
  }

  @Override
  public void prepareLog(@NotNull final String tag,
                         final int level,
                         @Nullable final Marker marker,
                         @Nullable final Throwable throwable,
                         @Nullable final StackTraceElement callerLocation,
                         @NotNull final LogMessageFormatter formatter,
                         @NotNull final String msg,
                         @NotNull final Object... formatArgs) {

  }
}
