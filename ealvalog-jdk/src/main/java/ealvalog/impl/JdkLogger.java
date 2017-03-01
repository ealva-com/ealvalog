package ealvalog.impl;

import ealvalog.Level;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;

import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Implementation that uses {@link java.util.logging.Logger}
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public class JdkLogger implements ealvalog.Logger {
  private java.util.logging.Logger jdkLogger;

  public JdkLogger(final String name) {
    jdkLogger = Logger.getLogger(name);
  }

  @NotNull @Override public String getName() {
    return jdkLogger.getName();
  }

  @Override public boolean isLoggable(@NotNull final Level level) {
    return jdkLogger.isLoggable(levelToJdkLevel(level));
  }

  @Override public boolean isLoggable(@NotNull final Level level, @NotNull final Marker marker) {
    return false;
  }

  @Override public void log(@NotNull final Level level, @NotNull final String msg) {
    jdkLogger.log(levelToJdkLevel(level), msg);
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
    if (isLoggable(level)) {
      jdkLogger.log(levelToJdkLevel(level), formatLogMsg(format, formatArgs), throwable);
    }
  }

  private String formatLogMsg(final @NotNull String format, final Object[] formatArgs) {
    return String.format(Locale.ROOT, format, formatArgs);
  }

  private java.util.logging.Level levelToJdkLevel(final Level level) {
    switch (level) {
      case TRACE:
        return java.util.logging.Level.FINEST;
      case DEBUG:
        return java.util.logging.Level.FINE;
      case INFO:
        return java.util.logging.Level.INFO;
      case WARN:
        return java.util.logging.Level.WARNING;
      case ERROR:
        return java.util.logging.Level.SEVERE;
      case CRITICAL:
        return java.util.logging.Level.SEVERE;
    }
    throw new IllegalArgumentException("Level must be from TRACE to CRITICAL");
  }

}
