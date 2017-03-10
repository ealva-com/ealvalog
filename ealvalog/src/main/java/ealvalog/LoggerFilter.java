package ealvalog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used at top layer of logging to prevent unnecessary calls into lower layers, hence lots of unnecessary objects being created
 * <p>
 * Created by Eric A. Snell on 3/6/17.
 */
public interface LoggerFilter {
  /**
   * Will a log at this {@link LogLevel} result in an actual log statement
   *
   * @param level the level to test, one of {@link LogLevel#TRACE}, {@link LogLevel#DEBUG}, {@link LogLevel#INFO}, {@link LogLevel#WARN},
   *              {@link LogLevel#ERROR}, {@link LogLevel#CRITICAL}
   *
   * @return true if a log statement will be produced at this level
   */
  boolean isLoggable(@NotNull LogLevel level);

  /**
   * Will a log at this {@link LogLevel}, with the given (optional) {@link Marker} and (optional) {@link Throwable}, result in an actual log
   * statement
   *
   * @param level     the level to test, one of {@link LogLevel#TRACE}, {@link LogLevel#DEBUG}, {@link LogLevel#INFO}, {@link
   *                  LogLevel#WARN}, {@link LogLevel#ERROR}, {@link LogLevel#CRITICAL}
   * @param marker    optional marker to test
   * @param throwable optional throwable to test
   *
   * @return true if a log statement will be produced at this level
   */
  boolean isLoggable(@NotNull LogLevel level, @Nullable Marker marker, @Nullable Throwable throwable);
}
