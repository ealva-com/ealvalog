package ealvalog.core;

import ealvalog.LogLevel;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Instance bridge the {@link CoreLogger} to the underlying logging implementation
 * <p>
 * Created by Eric A. Snell on 3/7/17.
 */
public interface Bridge {

  boolean getIncludeLocation();

  void setIncludeLocation(boolean includeLocation);

  /**
   * Invoked by the logger to see if we should proceed in logging
   *
   * @param level     client log level
   * @param marker    optional {@link Marker}
   * @param throwable optional {@link Throwable}
   *
   * @return true if logging should proceed
   */
  boolean isLoggable(@NotNull LogLevel level, @Nullable Marker marker, @Nullable Throwable throwable);

  /**
   * Proceed with logging. It's expected the framework has already invoked {@link #isLoggable(LogLevel, Marker, Throwable)} at the logger
   * level and this method need not check level or filter. Lover levels of the framework may still discard the log message.
   *
   * @param level      client log level
   * @param marker     optional {@link Marker}
   * @param throwable  optional {@link Throwable}
   * @param stackDepth depth of stack from client log site
   * @param msg        log message
   * @param formatArgs arguments to format the msg
   */
  void log(@NotNull LogLevel level,
           @Nullable Marker marker,
           @Nullable Throwable throwable,
           int stackDepth,
           @NotNull String msg,
           @NotNull Object... formatArgs);

  /**
   * Get the name of the Bridge implementation (typically same as the logger name)
   *
   * @return Bridge implementation name
   */
  String getName();
}
