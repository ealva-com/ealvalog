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

import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.util.Log;

import java.util.logging.LogRecord;

/**
 * Contained by the AndroidLogger to handle calling {@link Log}
 *
 * Created by Eric A. Snell on 3/3/17.
 */
public interface LogHandler {
  int MAX_LOG_LENGTH = 4000;

  boolean isLoggable(@NotNull String tag, int level);

  boolean shouldIncludeLocation(@NotNull String tag, int level, @Nullable Marker marker, @Nullable Throwable throwable);

  void prepareLog(String tag,
                  int level,
                  @Nullable Marker marker,
                  @Nullable Throwable throwable,
                  @Nullable StackTraceElement callerLocation,
                  @NotNull LogMessageFormatter formatter,
                  @NotNull String msg,
                  @NotNull Object... formatArgs);

  void prepareLog(@NotNull LogRecord record);
}
