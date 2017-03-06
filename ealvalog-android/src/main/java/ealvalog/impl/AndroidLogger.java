package ealvalog.impl;

import ealvalog.LogLevel;
import ealvalog.Marker;
import ealvalog.base.BaseLogger;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.util.Log;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logger implementation for Android
 * <p>
 * Created by Eric A. Snell on 3/3/17.
 */
@SuppressWarnings("WeakerAccess")
public class AndroidLogger extends BaseLogger {
  private static final ThreadLocal<LogMessageFormatter> threadLocalFormatter =
      new ThreadLocal<LogMessageFormatter>() {
        @Override
        protected LogMessageFormatter initialValue() {
          return new LogMessageFormatter();
        }

        @Override
        public LogMessageFormatter get() {
          LogMessageFormatter lmf = super.get();
          lmf.reset();
          return lmf;
        }
      };

  private static final int MAX_TAG_LENGTH = 23;
  private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

  private static AtomicReference<LogHandler> logHandler = new AtomicReference<>();

  public static void setHandler(@NotNull final LogHandler handler) {
    logHandler.set(handler);
  }

  public static LogHandler getHandler() {
    return logHandler.get();
  }

  public static void removeHandler() {
    logHandler.set(NullLogHandler.INSTANCE);
  }

  private final String name;
  private final String tag;

  public AndroidLogger(final String name) {
    this(name, null);
  }

  public AndroidLogger(final String name, final @Nullable Marker marker) {
    super(marker);
    this.name = name;
    tag = tagFromName(name);
  }

  private String tagFromName(final String name) {
    String tag = name;
    Matcher m = ANONYMOUS_CLASS.matcher(tag);
    if (m.find()) {
      tag = m.replaceAll("");
    }
    tag = tag.substring(tag.lastIndexOf('.') + 1);
    return tag.length() > MAX_TAG_LENGTH ? tag.substring(0, MAX_TAG_LENGTH) : tag;
  }

  @Override public @NotNull String getName() {
    return name;
  }

  @Override public boolean isLoggable(@NotNull final LogLevel level, @Nullable final Marker marker) {
    return logHandler.get().isLoggable(tag, levelToAndroidLevel(level));
  }

  @Override
  protected void printLog(final @NotNull LogLevel level,
                          final @Nullable Marker marker,
                          final @Nullable Throwable throwable,
                          final @Nullable StackTraceElement callerLocation,
                          final @NotNull String msg,
                          final @NotNull Object... formatArgs) {
    logHandler.get().prepareLog(tag,
                                levelToAndroidLevel(level),
                                marker,
                                throwable,
                                callerLocation,
                                threadLocalFormatter.get(),
                                msg,
                                formatArgs);
  }

  @Override
  protected boolean shouldIncludeLocation(@NotNull final LogLevel level,
                                          @Nullable final Marker marker,
                                          final @Nullable Throwable throwable) {
    return logHandler.get().shouldIncludeLocation(tag, levelToAndroidLevel(level), marker, throwable);
  }

  protected int levelToAndroidLevel(@NotNull final LogLevel level) {
    switch (level) {
      case TRACE:
        return Log.VERBOSE;
      case DEBUG:
        return Log.DEBUG;
      case INFO:
        return Log.INFO;
      case WARN:
        return Log.WARN;
      case ERROR:
        return Log.ERROR;
      case CRITICAL:
        return Log.ASSERT;
      default:
        throw new IllegalArgumentException("Illegal Level to map to Android");
    }
  }

}
