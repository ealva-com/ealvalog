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
