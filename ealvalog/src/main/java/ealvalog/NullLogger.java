package ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * A no-op {@link Logger} implementation
 *
 * Created by Eric A. Snell on 2/28/17.
 */
public enum NullLogger implements Logger {

  INSTANCE;

  @NotNull public String getName() {
    return "";
  }

  public boolean isLoggable(@NotNull final Level level) {
    return false;
  }

  @Override public boolean isLoggable(@NotNull final Level level, @NotNull final Marker marker) {
    return false;
  }

  public void log(@NotNull final Level level, @NotNull final String msg) {

  }

  public void log(@NotNull final Level level, @NotNull final Marker marker, @NotNull final String msg) {

  }

  public void log(@NotNull final Level level, @NotNull final Throwable throwable, @NotNull final String msg) {

  }

  public void log(@NotNull final Level level, @NotNull final Marker marker, @NotNull final Throwable throwable, @NotNull final String msg) {

  }

  public void log(@NotNull final Level level, @NotNull final String format, @NotNull final Object... formatArgs) {

  }

  public void log(@NotNull final Level level,
                  @NotNull final Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {

  }

  public void log(@NotNull final Level level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {

  }

  public void log(@NotNull final Level level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {

  }
}
