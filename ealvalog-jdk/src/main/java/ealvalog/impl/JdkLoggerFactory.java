/*
 * Copyright 2017 Eric A. Snell
 *
 * This file is part of eAlvaLog.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ealvalog.impl;

import ealvalog.LogLevel;
import ealvalog.Logger;
import ealvalog.LoggerFactory;
import ealvalog.LoggerFilter;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;
import java.util.logging.LogManager;

/**
 * Factory for {@link JdkLogger} instances
 * <p>
 * Created by Eric A. Snell on 3/4/17.
 */
public class JdkLoggerFactory implements LoggerFactory, JdkLoggerConfiguration {
  private final ConcurrentMap<String, JdkBridge> bridgeMap;
  private final ConcurrentMap<String, JdkLogger> loggerMap;
  private final JdkBridge jdkBridgeRoot;
  private final JdkLogger jdkRootLogger;
  private final Lock bridgeTreeLock = new ReentrantLock();

  private static volatile JdkLoggerFactory INSTANCE;
  @SuppressWarnings("WeakerAccess")
  public static JdkLoggerFactory instance() {
    if (INSTANCE == null) {
      synchronized (JdkLoggerFactory.class) {
        if (INSTANCE == null) {
          INSTANCE = new JdkLoggerFactory();
        }
      }
    }
    return INSTANCE;
  }

  private JdkLoggerFactory() {
    bridgeMap = new ConcurrentHashMap<>();
    loggerMap = new ConcurrentHashMap<>();
    jdkBridgeRoot = new JdkBridge(ROOT_LOGGER_NAME);
    jdkRootLogger = new JdkLogger(ROOT_LOGGER_NAME, null, this);
  }

  /**
   * Resets all loggers, removing filters and underlying handlers from the java.util.logging Loggers
   */
  @SuppressWarnings("WeakerAccess") public void reset() {
    LogManager.getLogManager().reset();
    final java.util.logging.Logger root = LogManager.getLogManager().getLogger("");
    final Handler[] handlers = root.getHandlers();
    for (Handler handler : handlers) {
      root.removeHandler(handler);
      handler.close();
    }

    for (JdkBridge bridge : bridgeMap.values()) {
      bridge.setToDefault();
    }

    bridgeMap.clear();
    setParents();
    updateLoggers();
  }

  @SuppressWarnings("WeakerAccess")
  public JdkLogger getRoot() {
    return jdkRootLogger;
  }

  @Override public @NotNull JdkLogger get(@NotNull final String name) {
    return getJdkLogger(name, null, false);
  }

  @Override public @NotNull JdkLogger get(@NotNull final String name, final boolean includeLocation) {
    return getJdkLogger(name, null, includeLocation);
  }

  @Override public @NotNull JdkLogger get(@NotNull final String name, @NotNull final Marker marker) {
    return getJdkLogger(name, marker, false);
  }

  @NotNull @Override public JdkLogger get(@NotNull final String name, @NotNull final Marker marker, final boolean includeLocation) {
    return getJdkLogger(name, marker, includeLocation);
  }

  private @NotNull JdkLogger getJdkLogger(final @NotNull String name, final @Nullable Marker marker, final boolean includeLocation) {
    if (ROOT_LOGGER_NAME.equals(name)) {
      return jdkRootLogger;
    }
    bridgeTreeLock.lock();
    try {
      JdkLogger jdkLogger = loggerMap.get(name);
      if (jdkLogger == null) {
        jdkLogger = new JdkLogger(name, marker, this);
        loggerMap.put(name, jdkLogger);
        setParents();
        if (includeLocation) {
          jdkLogger.setIncludeLocation(true);
        }
      }
      return jdkLogger;
    } finally {
      bridgeTreeLock.unlock();
    }
  }

  @Override public void setLoggerFilter(final @NotNull Logger logger, final @NotNull LoggerFilter filter) {
    bridgeTreeLock.lock();
    try {
      final String loggerName = logger.getName();
      final JdkBridge bridge = getBridge(loggerName);
      if (bridge.getName().equals(loggerName)) {
        bridge.setFilter(filter);
      } else {
        makeNewBridge(bridge, loggerName, filter, null, null);
      }
    } finally {
      bridgeTreeLock.unlock();
    }
  }


  @Override public void addLoggerHandler(final @NotNull Logger logger, final @NotNull BaseLoggerHandler loggerHandler) {
    bridgeTreeLock.lock();
    try {
      final String loggerName = logger.getName();
      final JdkBridge bridge = getBridge(loggerName);
      if (bridge.getName().equals(loggerName)) {
        bridge.addLoggerHandler(loggerHandler);
      } else {
        makeNewBridge(bridge, loggerName, null, loggerHandler, null);
      }
    } finally {
      bridgeTreeLock.unlock();
    }
  }

  @Override public void setLogLevel(@NotNull final Logger logger, @NotNull final LogLevel logLevel) {
    bridgeTreeLock.lock();
    try {
      final String loggerName = logger.getName();
      final JdkBridge bridge = getBridge(loggerName);
      if (bridge.getName().equals(loggerName)) {
        bridge.setLogLevel(logLevel);
      } else {
        makeNewBridge(bridge, loggerName, null, null, logLevel);
      }
    } finally {
      bridgeTreeLock.unlock();
    }
  }

  @Override public void setLogToParent(final Logger logger, final boolean logToParent) {
    bridgeTreeLock.lock();
    try {
      final String loggerName = logger.getName();
      final JdkBridge bridge = getBridge(loggerName);
      if (bridge.getName().equals(loggerName)) {
        bridge.setLogToParent(logToParent);
      } else {
        makeNewBridge(bridge, loggerName, null, null, null).setLogToParent(logToParent);
      }
    } finally {
      bridgeTreeLock.unlock();
    }
  }

  @Override public void setIncludeLocation(final Logger logger, final boolean includeLocation) {
    bridgeTreeLock.lock();
    try {
      final String loggerName = logger.getName();
      final JdkBridge bridge = getBridge(loggerName);
      if (bridge.getName().equals(loggerName)) {
        bridge.setIncludeLocation(true);
      } else {
        makeNewBridge(bridge, loggerName, null, null, null).setIncludeLocation(includeLocation);
      }
    } finally {
      bridgeTreeLock.unlock();
    }
  }

  private JdkBridge makeNewBridge(final @NotNull JdkBridge parent,
                                  final @NotNull String loggerName,
                                  final @Nullable LoggerFilter filter,
                                  final @Nullable BaseLoggerHandler handler,
                                  final @Nullable LogLevel logLevel) {
    final JdkBridge newBridge = new JdkBridge(loggerName, filter, handler, logLevel);
    newBridge.setParent(parent);
    bridgeMap.putIfAbsent(loggerName, newBridge);
    setParents();
    updateLoggers();
    return newBridge;
  }

  private void updateLoggers() {
    for (final JdkLogger logger : loggerMap.values()) {
      logger.update(this);
    }
  }

  @Override public JdkBridge getBridge(final String loggerClassName) {
    JdkBridge bridge = bridgeMap.get(loggerClassName);
    if (bridge != null) {
      return bridge;
    }
    String className = loggerClassName;
    while ((className = getParentName(className)) != null) {
      bridge = bridgeMap.get(className);
      if (bridge != null) {
        return bridge;
      }
    }
    return jdkBridgeRoot;
  }

  private static String getParentName(final @NotNull String name) {
    if (name.length() == 0) {
      return null;
    }
    final int i = name.lastIndexOf('.');
    return i > 0 ? name.substring(0, i) : "";
  }

  private void setParents() {
    for (final Map.Entry<String, JdkBridge> entry : bridgeMap.entrySet()) {
      final JdkBridge bridge = entry.getValue();
      String key = entry.getKey();
      if (!(key.length() == 0)) {
        final int i = key.lastIndexOf('.');
        if (i > 0) {
          key = key.substring(0, i);
          JdkBridge parent = getBridge(key);
          if (parent == null) {
            parent = jdkBridgeRoot;
          }
          bridge.setParent(parent);
        } else {
          bridge.setParent(jdkBridgeRoot);
        }
      }
    }
  }
}
