package ealvalog.impl;

import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation that logs everything and requests location information be provided
 * <p>
 * Created by Eric A. Snell on 3/3/17.
 */
public class DebugLogHandler extends BaseLogHandler {
  @Override public boolean isLoggable(@NotNull final String tag, final int level) {
    return true;
  }

  @Override
  public boolean shouldIncludeLocation(@NotNull final String tag,
                                       final int level,
                                       @Nullable final Marker marker,
                                       @Nullable final Throwable throwable) {
    return true;
  }

}
