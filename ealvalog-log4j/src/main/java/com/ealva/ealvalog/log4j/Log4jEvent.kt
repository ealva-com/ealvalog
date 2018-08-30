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

//import com.ealva.ealvalog.LogLevel
//import com.ealva.ealvalog.Marker
//import com.ealva.ealvalog.core.ExtLogRecord
//import org.apache.logging.log4j.Level
//import org.apache.logging.log4j.ThreadContext
//import org.apache.logging.log4j.core.LogEvent
//import org.apache.logging.log4j.core.impl.ThrowableProxy
//import org.apache.logging.log4j.core.time.Instant
//import org.apache.logging.log4j.core.time.MutableInstant
//import org.apache.logging.log4j.message.Message
//import org.apache.logging.log4j.message.ReusableMessageFactory
//import org.apache.logging.log4j.spi.DefaultThreadContextMap
//import org.apache.logging.log4j.util.ReadOnlyStringMap
//import java.io.Closeable

///**
// * Created by Eric A. Snell on 8/29/18.
// */
//class Log4jEvent private constructor() : LogEvent, Closeable {
//  lateinit var record: ExtLogRecord
//    private set
//
//  private constructor(extLogRecord: ExtLogRecord): this() {
//    record = extLogRecord
//  }
//
//  override fun getLevel(): Level {
//    return record.logLevel.log4jLevel
//  }
//
//  override fun getMessage(): Message {
//    return messageFactory.newMessage(record.message)
//  }
//
//  override fun getThreadName(): String {
//    return record.threadName
//  }
//
//  override fun getMarker(): org.apache.logging.log4j.Marker {
//    TODO()
////    return record.marker
//  }
//
//  override fun getInstant(): Instant {
//    return MutableInstant().apply { initFromEpochMilli(record.millis, 0) }
//  }
//
//  override fun getSource(): StackTraceElement? {
//    return record.location
//  }
//
//  override fun getNanoTime(): Long {
//    return record.nanoTime
//  }
//
//  override fun isIncludeLocation(): Boolean {
//    return record.location != null
//  }
//
//  override fun getContextMap(): Map<String, String> {
//    return mapOf()
//  }
//
//  override fun getLoggerName(): String {
//    return record.loggerName
//  }
//
//  override fun getThrown(): Throwable? {
//    return record.thrown
//  }
//
//  override fun setEndOfBatch(endOfBatch: Boolean) {}
//
//  override fun toImmutable(): LogEvent {
//    return Log4jEvent(record.copyOf())
//  }
//
//  override fun getTimeMillis(): Long {
//    return record.millis
//  }
//
//  override fun getThreadPriority(): Int {
//    return record.threadPriority
//  }
//
//  override fun getLoggerFqcn(): String {
//    return record.loggerName
//  }
//
//  override fun getContextData(): ReadOnlyStringMap {
//    return DefaultThreadContextMap()
//  }
//
//  override fun getContextStack(): ThreadContext.ContextStack {
//    return ThreadContext.EMPTY_STACK
//  }
//
//  override fun getThrownProxy(): ThrowableProxy {
//    return ThrowableProxy(record.thrown)
//  }
//
//  override fun getThreadId(): Long {
//    return record.threadID.toLong()
//  }
//
//  override fun isEndOfBatch(): Boolean {
//    return false
//  }
//
//  override fun setIncludeLocation(locationRequired: Boolean) {
//    record.addLocation(1)
//  }
//
//  override fun close() {
//    record.close()
//  }
//
//  companion object {
//    private val threadLocalEvent = ThreadLocal<Log4jEvent>()
//    private val messageFactory = ReusableMessageFactory.INSTANCE
//
//    private fun getEvent(): Log4jEvent {
//      var result: Log4jEvent? = threadLocalEvent.get()
//      if (result == null) {
//        result = Log4jEvent()
//        threadLocalEvent.set(result)
//      }
//      return result
//
//    }
//
//    fun get(loggerName: String, logLevel: LogLevel, marker: Marker?, throwable: Throwable?): Log4jEvent {
//      val event = getEvent()
//      // NOTE: set the record before doing anything else with the event as the record is lateinit
//      event.record = ExtLogRecord.get(logLevel, loggerName, marker, throwable)
//      return event
//    }
//  }
//
//}