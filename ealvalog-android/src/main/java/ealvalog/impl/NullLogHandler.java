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
