package ealvalog.impl;

import ealvalog.AlwaysYesFilter;
import ealvalog.LogLevel;
import ealvalog.LoggerFilter;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * Created by Eric A. Snell on 3/8/17.
 */
public class LoggerHandler extends Handler implements LoggerFilter {
  public static FileHandlerBuilder fileBuilder() {
    return new FileHandlerBuilder();
  }

  public static ConsoleHandlerBuilder consoleBuilder() {
    return new ConsoleHandlerBuilder();
  }

  private final Handler realHandler;
  private @NotNull LoggerFilter filter;

  LoggerHandler(final @NotNull Handler realHandler, final @NotNull LoggerFilter filter) {
    this.realHandler = realHandler;
    this.filter = filter;
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level) {
    return isLoggable(level, null, null);
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level, @Nullable final Marker marker, @Nullable final Throwable throwable) {
    final int levelValue = realHandler.getLevel().intValue();
    return !(level.getJdkLevel().intValue() < levelValue || levelValue == Level.OFF.intValue()) &&
        filter.isLoggable(level, marker, throwable);
  }

  public @NotNull LoggerFilter getLoggerFilter() {
    return filter;
  }

  public void setLoggerFilter(@Nullable final LoggerFilter filter) {
    this.filter = AlwaysYesFilter.nullToAlwaysYes(filter);
  }

  @Override public void publish(final LogRecord record) {
    realHandler.publish(record);
  }

  @Override public void flush() {
    realHandler.flush();
  }

  @Override public void close() throws SecurityException {
    realHandler.close();
  }
}
