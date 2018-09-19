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

import com.ealva.ealvalog.FilterResult
import com.ealva.ealvalog.LogEntry
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Logger
import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.core.Bridge
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import com.ealva.ealvalog.log4j.Log4jMarkerFactory.asLog4jMarker
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.LoggerConfig
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.impl.ContextDataFactory
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory
import org.apache.logging.log4j.core.lookup.Interpolator
import org.apache.logging.log4j.core.lookup.StrSubstitutor
import org.apache.logging.log4j.spi.ExtendedLogger
import org.apache.logging.log4j.spi.MutableThreadContextStack
import org.apache.logging.log4j.util.StringMap
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Eric A. Snell on 8/24/18.
 */
class Log4jBridge(
  override val name: String,
  private var filter: LoggerFilter = AlwaysNeutralFilter,
  logLevel: LogLevel? = null
) : Bridge {
  private val log4jLogger: ExtendedLogger =
    (LogManager.getLogger(name) as ExtendedLogger).apply {
      logLevel?.let {
        Configurator.setLevel(name, logLevel.log4jLevel)
      }
    }

  private val configuration = (LogManager.getContext(javaClass.classLoader, false) as LoggerContext)
    .configuration

  private val loggerConfig: LoggerConfig =
    configuration
      .getLoggerConfig(name)

  private val properties = ConcurrentHashMap<String, String>()
  private val tempLookup = Interpolator(properties)
  private val subst = StrSubstitutor(tempLookup)

  @field:Volatile var parent: Log4jBridge? = null  // root bridge will have a null parent

  override var includeLocation: Boolean = false

  override var logLevel: LogLevel
    get() = log4jLogger.level.logLevel
    set(logLevel) {
      Configurator.setLevel(log4jLogger.name, logLevel.log4jLevel)
    }

  override fun getFilter(): LoggerFilter {
    return filter
  }

  override fun setFilter(filter: LoggerFilter?) {
    this.filter = filter ?: AlwaysNeutralFilter
  }

  override fun shouldIncludeLocation(
    level: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): Boolean {
    return includeLocation || loggerConfig.isIncludeLocation
  }

  override fun willLogToParent(loggerName: String): Boolean {
    return loggerName == name && loggerConfig.isAdditive
  }

  override var logToParent: Boolean
    get() = loggerConfig.isAdditive
    set(value) {
      loggerConfig.isAdditive = value
    }

  override fun log(logEntry: LogEntry) {
    LogRecordEvent.fromLogEntry(logEntry).use { record ->
      loggerConfig.log(record.logEvent)
    }
  }

  override fun getLevelForLogger(logger: Logger): LogLevel? {
    return if (bridgeIsLoggerPeer(logger.name)) {
      log4jLogger.level.logLevel
    } else null
  }

  override fun bridgeIsLoggerPeer(loggerName: String): Boolean {
    return name == loggerName
  }

  override fun isLoggable(
    loggerName: String,
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult {
    return if (filter.isLoggable(loggerName, logLevel, marker, throwable) != FilterResult.DENY &&
      log4jLogger.isEnabled(logLevel.log4jLevel, asLog4jMarker(marker), "", throwable)
    ) FilterResult.ACCEPT
    else FilterResult.DENY
  }

  fun getRecordEvent(
    fqcn: String,
    logLevel: LogLevel,
    name: String,
    marker: Marker?,
    throwable: Throwable?,
    mdc: Map<String, String>?,
    ndc: List<String>?
  ): LogRecordEvent {
    return LogRecordEvent.getRecordEvent(fqcn, logLevel, name, marker, throwable).apply {
      setMdc(if (mdc != null) ReadOnlyStringMapAdapter(mdc) else createContextData(
        if (!loggerConfig.isPropertiesRequireLookup) {
          loggerConfig.propertyList ?: emptyList()
        } else {
          loggerConfig.propertyList?.mapTo(ArrayList(loggerConfig.propertyList.size)) {
            Property.createProperty(
              it.name,
              if (it.isValueNeedsLookup)
                subst.replace(this.logEvent, it.value)
              else
                it.value
            )
          } ?: emptyList()
        }))
      setNdc(
        when {
          ndc != null -> MutableThreadContextStack(ndc)
          ThreadContext.getDepth() == 0 -> NullThreadContextStack
          else -> ThreadContext.cloneStack()
        }
      )
    }
  }

  companion object {
    private val CONTEXT_DATA_INJECTOR = ContextDataInjectorFactory.createInjector()

    private fun createContextData(properties: List<Property>): StringMap {
      val reusable = ContextDataFactory.createContextData()
      return CONTEXT_DATA_INJECTOR.injectContextData(properties, reusable)
    }

  }

}
