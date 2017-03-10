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
    jdkRootLogger = new JdkLogger(ROOT_LOGGER_NAME, false, null, this);
  }

  /**
   * Resets all loggers, removing filters and underlying handlers from the java util logging Loggers
   */
  @SuppressWarnings("WeakerAccess") public void reset() {
    LogManager.getLogManager().reset();
    bridgeMap.clear();
    setParents();
    updateLoggers();
  }

  @Override public @NotNull JdkLogger get(@NotNull final String name) {
    return get(name, false, null);
  }

  @Override public @NotNull JdkLogger get(@NotNull final String name, final boolean includeLocation) {
    return get(name, includeLocation, null);
  }

  @Override public @NotNull JdkLogger get(@NotNull final String name, @NotNull final Marker marker) {
    return get(name, false, marker);
  }

  @NotNull @Override public Logger get(@NotNull final String name, @NotNull final Marker marker, final boolean includeLocation) {
    return get(name, includeLocation, marker);
  }

  private @NotNull JdkLogger get(@NotNull final String name, final boolean includeLocation, final @Nullable Marker marker) {
    if (ROOT_LOGGER_NAME.equals(name)) {
      return jdkRootLogger;
    }
    bridgeTreeLock.lock();
    try {
      JdkLogger jdkLogger = loggerMap.get(name);
      if (jdkLogger == null) {
        jdkLogger = new JdkLogger(name, includeLocation, marker, this);
        loggerMap.put(name, jdkLogger);
        setParents();
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
        final JdkBridge newBridge = new JdkBridge(loggerName);
        newBridge.setFilter(filter);
        newBridge.setParent(bridge);
        bridgeMap.putIfAbsent(loggerName, newBridge);
        setParents();
        updateLoggers();
      }
    } finally {
      bridgeTreeLock.unlock();
    }
  }


  @Override public void addLoggerHandler(final @NotNull Logger logger, final @NotNull LoggerHandler loggerHandler) {
    bridgeTreeLock.lock();
    try {
      final String loggerName = logger.getName();
      final JdkBridge bridge = getBridge(loggerName);
      if (bridge.getName().equals(loggerName)) {
        bridge.addLoggerHandler(loggerHandler);
      } else {
        final JdkBridge newBridge = new JdkBridge(loggerName);
        newBridge.addLoggerHandler(loggerHandler);
        newBridge.setParent(bridge);
        bridgeMap.putIfAbsent(loggerName, newBridge);
        setParents();
        updateLoggers();
      }
    } finally {
      bridgeTreeLock.unlock();
    }
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
