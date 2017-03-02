/*
 * Copyright 2017 Eric A. Snell
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

/**
 * Represents the logging level
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public enum Level {
  ALL(Integer.MIN_VALUE),
  TRACE(100),
  DEBUG(200),
  INFO(300),
  WARN(400),
  ERROR(500),
  CRITICAL(1000),
  NONE(Integer.MAX_VALUE);

  private final int level;

  Level(final int level) {
    this.level = level;
  }

  /**
   * Determine if {@code level} should be logged (is greater than or equal to this instance)
   *
   * @param level the level to test
   *
   * @return true if {@code level} is greater than or equal to this level
   */
  public boolean shouldLog(@NotNull final Level level) {
    return level.level >= this.level;
  }
}
