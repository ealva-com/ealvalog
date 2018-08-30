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

package com.ealva.ealvalog.log4j

import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.LoggerFactory
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by Eric A. Snell on 8/29/18.
 */
object Log4jLoggerFactory : LoggerFactory {
  private val bridgeMap: ConcurrentHashMap<String, Log4jBridge> = ConcurrentHashMap()
  private val loggerMap: ConcurrentMap<String, Log4jLoggerAdapter> = ConcurrentHashMap()
  private val bridgeTreeLock = ReentrantLock()
  private val bridgeRoot = Log4jBridge(LoggerFactory.ROOT_LOGGER_NAME)

  private val configuration: Log4jLoggerConfiguration = object :
    Log4jLoggerConfiguration {
    override fun setLoggerFilter(logger: Logger, filter: LoggerFilter) {
      setFilter(logger.name, filter)
    }

    override fun setLogLevel(logger: Logger, logLevel: LogLevel) {
      setLevel(logger.name, logLevel)
    }

    override fun setLogToParent(logger: Logger, logToParent: Boolean) {
      setShouldLogToParent(logger.name, logToParent)
    }

    override fun setIncludeLocation(logger: Logger, includeLocation: Boolean) {
      setShouldIncludeLocation(logger.name, includeLocation)
    }

    override fun getBridge(loggerClassName: String): Log4jBridge {
      return getTheLog4jBridge(loggerClassName)
    }
  }

  init {
    reset()
  }

  val root = Log4jLoggerAdapter(LoggerFactory.ROOT_LOGGER_NAME, null, configuration)

  /**
   * Typically not necessary for clients to call this as it's done on init. Resets all loggers,
   * removing filters and underlying handlers from the java.util.logging Loggers
   */
  fun reset() {
//    LogManager.getLogManager().reset()
//    val root = LogManager.getLogManager().getLogger("")
//    val handlers = root.handlers
//    for (handler in handlers) {
//      root.removeHandler(handler)
//      handler.close()
//    }
//
//    for (bridge in bridgeMap.values) {
//      bridge.setToDefault()
//    }

    bridgeMap.clear()
    loggerMap.clear()
    setParents()
    updateLoggers()
  }

  override fun get(name: String, marker: Marker?, includeLocation: Boolean): Log4jLoggerAdapter {
    if (LoggerFactory.ROOT_LOGGER_NAME == name) {
      return root
    }
    bridgeTreeLock.lock()
    try {
      var created = false
      val logger = loggerMap.getOrPut(name) {
        created = true // if we create a new one we need to ensure the parent hierarchy is correct
        Log4jLoggerAdapter(
          name,
          marker,
          configuration
        )
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

  override fun get(name: String): Log4jLoggerAdapter {
    return get(name, null, false)
  }

  private fun setFilter(loggerName: String, filter: LoggerFilter) {
    bridgeTreeLock.lock()
    try {
      val bridge = getTheLog4jBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.setFilter(filter)
      } else {
        makeNewBridge(
          bridge,
          loggerName,
          filter,
          null
        )
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun setLevel(loggerName: String, logLevel: LogLevel) {
    bridgeTreeLock.lock()
    try {
      val bridge = getTheLog4jBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.logLevel = logLevel
      } else {
        makeNewBridge(
          bridge,
          loggerName,
          null,
          logLevel
        )
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun setShouldLogToParent(loggerName: String, logToParent: Boolean) {
    bridgeTreeLock.lock()
    try {
      val bridge = getTheLog4jBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.logToParent = logToParent
      } else {
        makeNewBridge(bridge, loggerName, null, null).logToParent = logToParent
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun setShouldIncludeLocation(loggerName: String, includeLocation: Boolean) {
    bridgeTreeLock.lock()
    try {
      val bridge = getTheLog4jBridge(loggerName)
      if (bridge.name == loggerName) {
        bridge.includeLocation = includeLocation
      } else {
        makeNewBridge(bridge, loggerName, null, null).includeLocation = includeLocation
      }
    } finally {
      bridgeTreeLock.unlock()
    }
  }

  private fun makeNewBridge(
    parent: Log4jBridge,
    loggerName: String,
    filter: LoggerFilter?,
    logLevel: LogLevel?
  ): Log4jBridge {
    val newBridge =
      Log4jBridge(loggerName, filter ?: AlwaysNeutralFilter, logLevel)
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

  private fun getTheLog4jBridge(loggerClassName: String): Log4jBridge {
    var bridge: Log4jBridge? = bridgeMap[loggerClassName]
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
    return bridgeRoot
  }

  private fun setParents() {
    for (entry in bridgeMap.entries) {
      val bridge = entry.value
      var key = entry.key
      if (key.isNotEmpty()) {
        val i = key.lastIndexOf('.')
        if (i > 0) {
          key = key.substring(0, i)
          var parent: Log4jBridge? =
            getTheLog4jBridge(key)
          if (parent == null) {
            parent = bridgeRoot
          }
          bridge.parent = parent
        } else {
          bridge.parent = bridgeRoot
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