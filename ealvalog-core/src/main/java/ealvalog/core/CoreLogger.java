package ealvalog.core;

import ealvalog.LogLevel;
import ealvalog.LoggerFilter;
import ealvalog.Marker;
import ealvalog.base.BaseLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This logger delegates to a {@link Bridge} for {@link #isLoggable(LogLevel, Marker, Throwable)} and
 * {@link #printLog(LogLevel, Marker, Throwable, int, String, Object...)}
 * <p>
 * Created by Eric A. Snell on 3/7/17.
 */
public abstract class CoreLogger<T extends Bridge> extends BaseLogger {
  private volatile @NotNull T bridge;

  @SuppressWarnings("unused")
  protected CoreLogger(final @NotNull String name, final @NotNull T bridge) {
    this(name, bridge, null);
  }

  protected CoreLogger(final @NotNull String name, final @NotNull T bridge, @Nullable final Marker marker) {
    super(name, marker);
    this.bridge = bridge;
  }

  @Override public boolean isLoggable(final @NotNull LogLevel level, @Nullable final Marker marker, @Nullable final Throwable throwable) {
    return bridge.isLoggable(level, marker, throwable);
  }

  @Override
  protected void printLog(@NotNull final LogLevel level,
                          @Nullable final Marker marker,
                          @Nullable final Throwable throwable,
                          final int stackDepth,
                          @NotNull final String msg,
                          @NotNull final Object... formatArgs) {
    // isLoggable() should have already been called
    bridge.log(level, marker, throwable, stackDepth + 1, msg, formatArgs);
  }

  protected void setBridge(@NotNull final T bridge) {
    this.bridge = bridge;
  }

  protected @NotNull T getBridge() {
    return bridge;
  }

  public abstract void setFilter(@NotNull LoggerFilter filter);

  @Override public void setIncludeLocation(final boolean includeLocation) {
    bridge.setIncludeLocation(includeLocation);
  }

  @Override public boolean getIncludeLocation() {
    return bridge.getIncludeLocation();
  }
}
