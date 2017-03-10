package ealvalog.impl;

import ealvalog.AlwaysYesFilter;
import ealvalog.LogLevel;
import ealvalog.LoggerFilter;
import ealvalog.Marker;
import ealvalog.base.LogUtil;
import ealvalog.core.Bridge;
import ealvalog.core.CoreLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Bridge the {@link CoreLogger} to the underlying java.util.logging.Logger
 * <p>
 * Created by Eric A. Snell on 3/7/17.
 */
public class JdkBridge implements Bridge {
  private volatile JdkBridge parent;  // root bridge will have a null parent
  private final @NotNull java.util.logging.Logger jdkLogger;
  private volatile LoggerFilter filter;
  private volatile List<LoggerHandler> handlerList;
  private boolean jdkLoggerHasHandlers;
  private boolean includeLocation;


  JdkBridge(final @NotNull String name) {
    parent = null;
    jdkLogger = Logger.getLogger(name);
    filter = AlwaysYesFilter.INSTANCE;
    handlerList = new CopyOnWriteArrayList<>();
    jdkLoggerHasHandlers = false;
    includeLocation = false;
    final Handler[] handlers = jdkLogger.getHandlers();
    if (handlers != null) {
      for (int i = 0; i < handlers.length; i++) {
        jdkLoggerHasHandlers = true;
        final Handler handler = handlers[i];
        if (handler instanceof LoggerHandler) {
          handlerList.add((LoggerHandler)handler);
        }
      }
    }
  }

  @Nullable JdkBridge getParent() {
    return parent;
  }

  void setParent(final @Nullable JdkBridge parent) {
    this.parent = parent;
  }

  @Nullable LoggerFilter getFilter() {
    return filter;
  }

  void setFilter(final @Nullable LoggerFilter filter) {
    this.filter = AlwaysYesFilter.nullToAlwaysYes(filter);
  }

  /** @return the include location flag */
  @Override public boolean getIncludeLocation() {
    return includeLocation;
  }

  /** Set the include location flag */
  @Override public void setIncludeLocation(final boolean includeLocation) {
    this.includeLocation = includeLocation;
  }

  private boolean shouldIncludeLocation() {
    return includeLocation || (parent != null && parent.includeLocation);
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method checks against the level in the Jdk logger, any contained filter, and Jdk logger {@link Handler}s of the type
   * {@link LoggerHandler}. If the real Jdk logger does not contain any handlers, the parent, if any, of this bridge is checked.
   */
  @Override public boolean isLoggable(final @NotNull LogLevel level, final @Nullable Marker marker, final @Nullable Throwable throwable) {
    if (!jdkLogger.isLoggable(level.getJdkLevel())) {
      return false;
    }
    if (!filter.isLoggable(level, marker, throwable)) {
      return false;
    }
    if (jdkLoggerHasHandlers) {
      for (LoggerHandler loggerHandler : handlerList) {
        if (!loggerHandler.isLoggable(level, marker, throwable)) {
          return false;
        }
      }
    } else if (parent != null && !parent.isLoggable(level, marker, throwable)) {
      return false;
    }
    return true;
  }

  @Override
  public void log(final @NotNull LogLevel level,
                  final @Nullable Marker marker,
                  final @Nullable Throwable throwable,
                  final int stackDepth,
                  final @NotNull String msg,
                  final @NotNull Object... formatArgs) {
    // ENSURE the record obtained is released!
    //
    // We're not using try with resources here due to warnings about early Android versions.
    ExtLogRecord logRecord = ExtLogRecord.get(level,
                                              msg,
                                              getName(),
                                              shouldIncludeLocation() ? LogUtil.getCallerLocation(stackDepth + 1)
                                                                      : null,
                                              throwable,
                                              formatArgs);
    try {
      doLog(logRecord);
    } finally {
      ExtLogRecord.release(logRecord);
    }
  }

  // visible for test
  @SuppressWarnings("WeakerAccess")
  void doLog(final ExtLogRecord logRecord) {
    jdkLogger.log(logRecord);
  }

  @Override public String getName() {
    return jdkLogger.getName();
  }

  void addLoggerHandler(final LoggerHandler loggerHandler) {
    handlerList.add(loggerHandler);
  }
}
