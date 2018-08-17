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

package com.ealva.ealvalog.impl

import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.LoggerFactory
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Handler
import java.util.logging.LogManager

/**
 * Factory for [JdkLogger] instances
 *
 *
 * Created by Eric A. Snell on 3/4/17.
 */
object JdkLoggerFactory : LoggerFactory {
  private val bridgeMap: ConcurrentHashMap<String, JdkBridge> = ConcurrentHashMap()
  private val loggerMap: ConcurrentMap<String, JdkLogger> = ConcurrentHashMap()
  private val bridgeTreeLock = ReentrantLock()
  private val jdkBridgeRoot = JdkBridge(LoggerFactory.ROOT_LOGGER_NAME)

  private val configuration: JdkLoggerConfiguration = object : JdkLoggerConfiguration {
    override fun setLoggerFilter(logger: Logger, filter: LoggerFilter) {
      setFilter(logger, filter)
    }

    override fun addLoggerHandler(logger: Logger, loggerHandler: Handler) {
      addHandler(logger, loggerHandler)
    }

    override fun setLogLevel(logger: Logger, logLevel: LogLevel) {
      setLevel(logger, logLevel)
    }

    override fun setLogToParent(logger: Logger, logToParent: Boolean) {
      setShouldLogToParent(logger, logToParent)
    }

    override fun setIncludeLocation(logger: Logger, includeLocation: Boolean) {
      setShouldIncludeLocation(logger, includeLocation)
    }

    override fun getBridge(loggerClassName: String): JdkBridge {
      return getTheJdkBridge(loggerClassName)
    }
  }

  init {
    reset()
  }

  val root = JdkLogger(LoggerFactory.ROOT_LOGGER_NAME, null, configuration)

  /**
   * Typically not necessary for clients to call this as it's done on init. Resets all loggers,
   * removing filters and underlying handlers from the java.util.logging Loggers
   */
  fun reset() {
    LogManager.getLogManager().reset()
    val root = LogManager.getLogManager().getLogger("")
    val handlers = root.handlers
    for (handler in handlers) {
      root.removeHandler(handler)
      handler.close()
    }

    for (bridge in bridgeMap.values) {
      bridge.setToDefault()
    }

    bridgeMap.clear()
    loggerMap.clear()
    setParents()
    updateLoggers()
  }

  override fun get(name: String, marker: Marker?, includeLocation: Boolean): JdkLogger {
    if (LoggerFactory.ROOT_LOGGER_NAME == name) {
      return root
    }
    bridgeTreeLock.lock()
    try {
      var created = false
      val logger = loggerMap.getOrPut(name) {
        created = true // if we create a new one we need to ensure the parent hierarchy is correct
        JdkLogger(name, marker, configuration)
      }
      if (created) {
        setParents()
        if (includeLocation) {
          logger.includeLocation = true
        }
      }
      return logger
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  override fun get(name: String): JdkLogger {
    return get(name, null, false)
  }

  private fun setFilter(logger: Logger, filter: LoggerFilter) {
    bridgeTreeLock.lock()
    try {
      val loggerName = logger.name
      val bridge = getTheJdkBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.setFilter(filter)
      } else {
        makeNewBridge(bridge, loggerName, filter, null, null)
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun addHandler(logger: Logger, loggerHandler: Handler) {
    bridgeTreeLock.lock()
    try {
      val loggerName = logger.name
      val bridge = getTheJdkBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.addLoggerHandler(loggerHandler)
      } else {
        makeNewBridge(bridge, loggerName, null, loggerHandler, null)
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun setLevel(logger: Logger, logLevel: LogLevel) {
    bridgeTreeLock.lock()
    try {
      val loggerName = logger.name
      val bridge = getTheJdkBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.logLevel = logLevel
      } else {
        makeNewBridge(bridge, loggerName, null, null, logLevel)
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun setShouldLogToParent(logger: Logger, logToParent: Boolean) {
    bridgeTreeLock.lock()
    try {
      val loggerName = logger.name
      val bridge = getTheJdkBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.setLogToParent(logToParent)
      } else {
        makeNewBridge(bridge, loggerName, null, null, null).setLogToParent(logToParent)
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun setShouldIncludeLocation(logger: Logger, includeLocation: Boolean) {
    bridgeTreeLock.lock()
    try {
      val loggerName = logger.name
      val bridge = getTheJdkBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.includeLocation = includeLocation
      } else {
        makeNewBridge(bridge, loggerName, null, null, null).includeLocation = includeLocation
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun makeNewBridge(
    parent: JdkBridge,
    loggerName: String,
    filter: LoggerFilter?,
    handler: Handler?,
    logLevel: LogLevel?
  ): JdkBridge {
    val newBridge = JdkBridge(loggerName, filter ?: AlwaysNeutralFilter, handler, logLevel)
    newBridge.parent = parent
    bridgeMap.putIfAbsent(loggerName, newBridge)
    setParents()
    updateLoggers()
    return newBridge
  }

  private fun updateLoggers() {
    for (logger in loggerMap.values) {
      logger.update(configuration)
    }
  }

  private fun getTheJdkBridge(loggerClassName: String): JdkBridge {
    var bridge: JdkBridge? = bridgeMap[loggerClassName]
    if (bridge != null) {
      return bridge
    }
    var className: String? = getParentName(loggerClassName)
    while (className != null) {
      bridge = bridgeMap[className]
      if (bridge != null) {
        return bridge
      }
      className = getParentName(className)
    }
    return jdkBridgeRoot
  }

  private fun setParents() {
    for (entry in bridgeMap.entries) {
      val bridge = entry.value
      var key = entry.key
      if (key.isNotEmpty()) {
        val i = key.lastIndexOf('.')
        if (i > 0) {
          key = key.substring(0, i)
          var parent: JdkBridge? = getTheJdkBridge(key)
          if (parent == null) {
            parent = jdkBridgeRoot
          }
          bridge.parent = parent
        } else {
          bridge.parent = jdkBridgeRoot
        }
      }
    }
  }

  private fun getParentName(name: String): String? {
    if (name.isEmpty()) {
      return null
    }
    val i = name.lastIndexOf('.')
    return if (i > 0) name.substring(0, i) else ""
  }
}
