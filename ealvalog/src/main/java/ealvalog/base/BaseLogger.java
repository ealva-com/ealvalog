package ealvalog.base;

import ealvalog.Level;
import ealvalog.Logger;
import ealvalog.Marker;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Eric A. Snell on 3/1/17.
 */
public abstract class BaseLogger implements Logger {

  @Override public boolean isLoggable(@NotNull final Level level) {
    return isLoggable(level, null);
  }

  @Override public void log(@NotNull final Level level, @NotNull final String msg) {

  }

  @Override public void log(@NotNull final Level level, @NotNull final Marker marker, @NotNull final String msg) {

  }

  @Override public void log(@NotNull final Level level, @NotNull final Throwable throwable, @NotNull final String msg) {

  }

  @Override
  public void log(@NotNull final Level level, @NotNull final Marker marker, @NotNull final Throwable throwable, @NotNull final String msg) {

  }

  @Override public void log(@NotNull final Level level, @NotNull final String format, @NotNull final Object... formatArgs) {

  }

  @Override
  public void log(@NotNull final Level level,
                  @NotNull final Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {

  }

  @Override
  public void log(@NotNull final Level level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {

  }

  @Override
  public void log(@NotNull final Level level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {

  }


  protected abstract void doLog(@NotNull final Level level,
                                @Nullable final Marker marker,
                                @Nullable final Throwable throwable,
                                @Nullable final StackTraceElement callerLocation,
                                @NotNull final LogMessageFormatter formatter,
                                @NotNull final String msg,
                                @Nullable final Object[] formatArgs);

  protected abstract boolean shouldIncludeLocation();
}
