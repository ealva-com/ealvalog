package ealvalog.impl;

import ealvalog.LoggerFactory;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.LogManager;

/**
 * Factory for {@link JdkLogger} instances
 *
 * Created by Eric A. Snell on 3/4/17.
 */
public class JdkLoggerFactory implements LoggerFactory {
  private final @NotNull ConcurrentMap<String, JdkLogger> nameToLoggerMap;

  public JdkLoggerFactory() {
    nameToLoggerMap = new ConcurrentHashMap<>();
    final JdkLogger rootLogger = new JdkLogger(LoggerFactory.ROOT_LOGGER_NAME);
    nameToLoggerMap.put(rootLogger.getName(), rootLogger);
  }

  public void reset() {
    LogManager.getLogManager().reset();
  }

  @Override public @NotNull JdkLogger get(@NotNull final String name) {
    synchronized (JdkLoggerFactory.class) {
      JdkLogger jdkLogger = nameToLoggerMap.get(name);
      if (jdkLogger == null) {
        jdkLogger = new JdkLogger(name);
        nameToLoggerMap.put(name, jdkLogger);
      }
      return jdkLogger;
    }
  }

  @Override public @NotNull JdkLogger get(@NotNull final String name, @NotNull final Marker marker) {
    synchronized (JdkLoggerFactory.class) {
      JdkLogger jdkLogger = nameToLoggerMap.get(name);
      if (jdkLogger == null) {
        jdkLogger = new JdkLogger(name, marker);
        nameToLoggerMap.put(name, jdkLogger);
      }
      return jdkLogger;
    }
  }
}
