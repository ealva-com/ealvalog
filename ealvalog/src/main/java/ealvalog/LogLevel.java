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
  ALL(java.util.logging.Level.ALL),
  TRACE(java.util.logging.Level.FINEST),
  DEBUG(java.util.logging.Level.FINE),
  INFO(java.util.logging.Level.CONFIG),
  WARN(java.util.logging.Level.INFO),
  ERROR(java.util.logging.Level.WARNING),
  CRITICAL(java.util.logging.Level.SEVERE),
  NONE(java.util.logging.Level.OFF);

  private static final Map<Level, LogLevel> levelToLogLevelMap;
  static {
    levelToLogLevelMap = new HashMap<>(8);
    final LogLevel[] logLevels = values();
    for (LogLevel logLevel : logLevels) {
      levelToLogLevelMap.put(logLevel.getLevel(), logLevel);
    }
  }

  public static @NotNull LogLevel fromLevel(@NotNull final Level level) {
    final LogLevel logLevel = levelToLogLevelMap.get(level);
    return logLevel == null ? NONE : logLevel;
  }

  private final Level level;

  LogLevel(final Level level) {
    this.level = level;
  }

  public Level getLevel() {
    return level;
  }
}
