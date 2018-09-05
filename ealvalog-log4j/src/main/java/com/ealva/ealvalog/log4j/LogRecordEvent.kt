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

import com.ealva.ealvalog.LogEntry
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.core.ExtLogRecord
import com.ealva.ealvalog.e
import com.ealva.ealvalog.invoke
import com.ealva.ealvalog.lazyLogger
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.impl.ThrowableProxy
import org.apache.logging.log4j.core.time.Instant
import org.apache.logging.log4j.core.time.MutableInstant
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.message.ReusableMessageFactory
import org.apache.logging.log4j.spi.DefaultThreadContextMap
import org.apache.logging.log4j.util.ReadOnlyStringMap

private val LOG by lazyLogger(LogRecordEvent::class)

/**
 * Created by Eric A. Snell on 8/29/18.
 */
class LogRecordEvent(logEntry: LogEntry?) : ExtLogRecord(logEntry) {
  val logEvent: LogEvent = object : LogEvent {
    override fun getLevel(): Level {
      return logLevel.log4jLevel
    }

    /**
     * We are obtaining a thread local [org.apache.logging.log4j.message.ReusableSimpleMessage] or
     * [org.apache.logging.log4j.message.ParameterizedMessage] from the factory, so any client
     * should not use the returned [Message] past the lifetime of the LogRecordEvent.
     */
    override fun getMessage(): Message {
      return if (parameterCount == 0) {
        messageFactory.newMessage(this@LogRecordEvent.message)
      } else {
        messageFactory.newMessage(this@LogRecordEvent.message, *parameters)
      }
    }

    override fun getThreadName(): String {
      return this@LogRecordEvent.threadName
    }

    override fun getMarker(): org.apache.logging.log4j.Marker {
      TODO()
//    return record.marker
    }

    override fun getInstant(): Instant {
      return MutableInstant().apply { initFromEpochMilli(millis, 0) }
    }

    override fun getSource(): StackTraceElement? {
      return location
    }

    override fun getNanoTime(): Long {
      return this@LogRecordEvent.nanoTime
    }

    /**
     * We have either already included the location at the point this event was "created" or
     * it is not needed. We don't want downstream components trying to determine stack
     * position of the original log call (client code)
     */
    override fun isIncludeLocation(): Boolean {
      return false
    }

    @Deprecated(
      "Use getContextData()",
      ReplaceWith("getContextData()", "org.apache.logging.log4j.util.ReadOnlyStringMap")
    )
    override fun getContextMap(): Map<String, String> {
      return contextData.toMap()
    }

    override fun getLoggerName(): String {
      return this@LogRecordEvent.loggerName
    }

    override fun getThrown(): Throwable? {
      return this@LogRecordEvent.thrown
    }

    override fun setEndOfBatch(endOfBatch: Boolean) {}

    override fun toImmutable(): LogEvent {
      return LogRecordEvent(this@LogRecordEvent).logEvent
    }

    override fun getTimeMillis(): Long {
      return millis
    }

    override fun getThreadPriority(): Int {
      return this@LogRecordEvent.threadPriority
    }

    override fun getLoggerFqcn(): String {
      return loggerName
    }

    override fun getContextData(): ReadOnlyStringMap {
      return DefaultThreadContextMap()
    }

    override fun getContextStack(): ThreadContext.ContextStack {
      return ThreadContext.EMPTY_STACK
    }

    override fun getThrownProxy(): ThrowableProxy? {
      this@LogRecordEvent.thrown?.let { thrown ->
        return ThrowableProxy(thrown)
      } ?: return null
    }

    override fun getThreadId(): Long {
      return this@LogRecordEvent.threadID.toLong()
    }

    override fun isEndOfBatch(): Boolean {
      return false
    }

    override fun setIncludeLocation(locationRequired: Boolean) {}
  }

  override fun reserve(): LogRecordEvent {
    super.reserve()
    return this
  }

  companion object {
    /**
     * Returns [entry] if it is already a LogRecordEvent, else creates a new LogRecordEvent.
     * The only time a new LogRecordEvent will be created is if a client logs a [LogEntry] it
     * did not originally obtain from the [com.ealva.ealvalog.Logger] to which it is logging.
     * See [com.ealva.ealvalog.Logger.getLogEntry]
     */
    fun fromLogEntry(entry: LogEntry): LogRecordEvent {
      return entry as? LogRecordEvent ?: LogRecordEvent(entry)
    }

    private val messageFactory = ReusableMessageFactory.INSTANCE

    private val threadLocalRecord = ThreadLocal<LogRecordEvent>()

    fun getRecordEvent(
      logLevel: LogLevel,
      name: String,
      marker: Marker?,
      throwable: Throwable?
    ): LogRecordEvent {
      var result: LogRecordEvent? = threadLocalRecord.get()
      if (result == null) {
        result = LogRecordEvent(null)
        threadLocalRecord.set(result)
      }
      return (if (result.isReserved) makeNewAndReserve() else result.reserve()).apply {
        setLogLevel(logLevel)
        setLoggerName(name)
        this.marker = marker
        setThrown(throwable)
      }
    }

    private fun makeNewAndReserve(): LogRecordEvent {
      LOG.e {
        it(
          "Had to make a new LogRecordEvent because the thread local is already in use. In use=%s",
          threadLocalRecord.get() ?: "Null?"
        )
      }
      return LogRecordEvent(null).reserve()
    }

  }
}