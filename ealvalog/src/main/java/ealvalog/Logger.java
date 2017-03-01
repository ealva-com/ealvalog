package ealvalog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * It's expected all logging occurs through concrete implementations of this interface which are obtained via {@link TheLoggerFactory}
 *
 * Created by Eric A. Snell on 2/28/17.
 */
public interface Logger {

  @NotNull String getName();

  boolean isLoggable(@NotNull Level level);

  boolean isLoggable(@NotNull Level level, @Nullable Marker marker);

  void log(@NotNull Level level, @NotNull String msg);

  void log(@NotNull Level level, @NotNull Marker marker, @NotNull String msg);

  void log(@NotNull Level level, @NotNull Throwable throwable, @NotNull String msg);

  void log(@NotNull Level level, @NotNull Marker marker, @NotNull Throwable throwable, @NotNull String msg);

  void log(@NotNull Level level, @NotNull String format, @NotNull Object... formatArgs);

  void log(@NotNull Level level, @NotNull Marker marker, @NotNull String format, @NotNull Object... formatArgs);

  void log(@NotNull Level level, @NotNull Throwable throwable, @NotNull String format, @NotNull Object... formatArgs);

  void log(@NotNull Level level,
           @NotNull Marker marker,
           @NotNull Throwable throwable,
           @NotNull String format,
           @NotNull Object... formatArgs);

}
