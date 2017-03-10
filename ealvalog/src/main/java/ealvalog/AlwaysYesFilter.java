package ealvalog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A filter that always responds true. Helps avoid null.
 *
 * Created by Eric A. Snell on 3/8/17.
 */
public enum AlwaysYesFilter implements LoggerFilter {
  INSTANCE;

  /**
   * If {@code filter} is null, returns an AlwaysYesFilter, else returns {@code filter}
   *
   * @param filter optional filter
   *
   * @return the filter parameter if not null, else {@link AlwaysYesFilter#INSTANCE}
   */
  public static @NotNull LoggerFilter nullToAlwaysYes(final @Nullable LoggerFilter filter) {
    return filter == null ? INSTANCE : filter;
  }

  /**
   * {@inheritDoc}

   * @return always true
   */
  @Override public boolean isLoggable(@NotNull final LogLevel level) {
    return true;
  }

  /**
   * {@inheritDoc}
   * @return always returns true
   */
  @Override public boolean isLoggable(final @NotNull LogLevel level, final @Nullable Marker marker, final @Nullable Throwable throwable) {
    return true;
  }

}
