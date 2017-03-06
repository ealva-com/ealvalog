package ealvalog.impl;

import ealvalog.Marker;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.util.Log;

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
}
