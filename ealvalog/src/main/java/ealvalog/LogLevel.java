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

package ealvalog;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Represents the logging level
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public enum LogLevel {
  ALL(Integer.MIN_VALUE, java.util.logging.Level.ALL),
  TRACE(1000, java.util.logging.Level.FINEST),
  DEBUG(2000,java.util.logging.Level.FINE),
  INFO(3000, java.util.logging.Level.CONFIG),
  WARN(4000, java.util.logging.Level.INFO),
  ERROR(5000, java.util.logging.Level.WARNING),
  CRITICAL(6000, java.util.logging.Level.SEVERE),
  NONE(Integer.MAX_VALUE, java.util.logging.Level.OFF);

  private static final Map<Level, LogLevel> levelToLogLevelMap;
  static {
    levelToLogLevelMap = new HashMap<>(8);
    final LogLevel[] logLevels = values();
    for (LogLevel logLevel : logLevels) {
      levelToLogLevelMap.put(logLevel.getJdkLevel(), logLevel);
    }
  }

  public static @NotNull LogLevel fromLevel(@NotNull final Level level) {
    final LogLevel logLevel = levelToLogLevelMap.get(level);
    return logLevel == null ? NONE : logLevel;
  }

  private final int value;
  private final Level level;

  LogLevel(final int value, final Level level) {
    this.value = value;
    this.level = level;
  }

  public Level getJdkLevel() {
    return level;
  }

  public boolean isAtLeast(final @NotNull LogLevel level) {
    return value >= level.value;
  }
}
