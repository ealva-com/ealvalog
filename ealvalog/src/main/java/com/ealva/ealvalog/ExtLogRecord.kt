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

package com.ealva.ealvalog

import com.ealva.ealvalog.util.LogUtil
import org.jetbrains.annotations.TestOnly

import java.io.Closeable
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Arrays
import java.util.Formatter
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level
import java.util.logging.LogRecord

/**
 * Subclass of LogRecord adding the extra info we need. Not thread safe.
 *
 *
 * Use [.getRecord] to obtain an ExtLogRecord which is associated with a thread and needs
 * to be [.release]ed to be properly reused. Lower layers of the logging framework
 * need to copy this ExtLogRecord if it's to be passed to another thread.
 *
 *
 * Don't use the [.getParameters] array length as the actual number of parameters. Use
 * [.getParameterCount] instead. There might be nulls at the end of the array due to reuse
 *
 *
 * Created by Eric A. Snell on 3/4/17.
 */
class ExtLogRecord private constructor() : LogRecord(Level.OFF, ""),
  Closeable, // not AutoCloseable to be compatible with Android version < KitKat
  LogRecordBuilder {

  // Everything is transient as we will handle read/write during serialization
  @Transient private var logLevel: LogLevel
  /**
   * Name of the thread on which this instance was constructed
   *
   * @return thread name
   */
  @Transient var threadName: String
  @Transient private var marker: Marker
  @Transient var callLocation: StackTraceElement? = null
    private set
  /** @return the number of parameters passed to [.setParameters]
   */
  @Transient var parameterCount: Int = 0
    private set   // actual number of parameters, array might be over-sized
  @Transient private var isReserved: Boolean = false
  @Transient private var builder: StringBuilder
  @Transient private var formatter: Formatter

  init {
    parameters = null
    logLevel = LogLevel.NONE
    threadName = Thread.currentThread().name
    marker = NullMarker
    callLocation = null
    parameterCount = 0
    isReserved = false
    builder = StringBuilder(DEFAULT_STRING_BUILDER_SIZE)
    formatter = Formatter(builder)
  }

  private fun reserve(): ExtLogRecord {
    isReserved = true
    logLevel = LogLevel.NONE
    marker = NullMarker
    callLocation = null
    parameters = null
    millis = System.currentTimeMillis()
    sequenceNumber = sequence.getAndIncrement()
    if (builder.capacity() > maxBuilderSize) {
      builder.setLength(maxBuilderSize)
      builder.trimToSize()
    }
    builder.setLength(0)
    return this
  }

  private fun release() {
    isReserved = false
  }

  fun setLocation(location: StackTraceElement?) {
    this.callLocation = location
  }

  override fun getMessage(): String {
    return builder.toString()
  }

  override fun setMessage(message: String?) {
    val builder = this.builder
    builder.setLength(0)
    builder.append(message)
  }

  override fun setParameters(parameters: Array<Any>?) {
    val existingParameters = getParameters()
    if (parameters != null) {
      parameterCount = parameters.size
      if (existingParameters != null && existingParameters.size >= parameters.size) {
        System.arraycopy(parameters, 0, existingParameters, 0, parameters.size)
        if (existingParameters.size > parameters.size) {
          Arrays.fill(existingParameters, parameters.size, existingParameters.size, null)
        }
        // in current impl this is redundant, but let's not assume LogRecord never changes
        super.setParameters(existingParameters)
      } else {
        super.setParameters(Arrays.copyOf(parameters, parameters.size))
      }
    } else {
      parameterCount = 0
      if (existingParameters != null && existingParameters.isNotEmpty()) {
        Arrays.fill(existingParameters, null)
      }
    }
  }

  fun getLogLevel(): LogLevel {
    return logLevel
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun setLogLevel(level: LogLevel): ExtLogRecord {
    logLevel = level
    setLevel(level.jdkLevel)
    return this
  }

  /**
   * Returns the associated marker, which may be [NullMarker] if no marker was set.
   *
   * @return the [Marker] set into this record, or [NullMarker] if no contained marker
   */
  fun getMarker(): Marker {
    return marker
  }

  /** Set the marker, or clear it to [NullMarker] if null is passed  */
  fun setMarker(marker: Marker?) {
    this.marker = marker ?: NullMarker
  }

  override fun close() {
    release(this)
  }

  @Throws(IOException::class, ClassNotFoundException::class)
  private fun readObject(`in`: ObjectInputStream) {
    `in`.defaultReadObject()
    logLevel = `in`.readObject() as LogLevel
    threadName = `in`.readUTF()
    marker = `in`.readObject() as Marker
    callLocation = `in`.readObject() as StackTraceElement
    parameterCount = `in`.readInt()
    isReserved = false
    builder = StringBuilder(DEFAULT_STRING_BUILDER_SIZE)
    formatter = Formatter(builder)
  }

  @Throws(IOException::class)
  private fun writeObject(out: ObjectOutputStream) {
    out.defaultWriteObject()
    out.writeObject(logLevel)
    out.writeUTF(threadName)
    out.writeObject(marker)
    out.writeObject(callLocation)
    out.writeInt(parameterCount)
  }

  @Suppress("unused")
  fun copyOf(): ExtLogRecord {
    val copy = ExtLogRecord()
    copy.setLogLevel(logLevel)  // handles Level and LogLevel
    copy.sequenceNumber = sequenceNumber
    copy.sourceClassName = sourceClassName
    copy.sourceMethodName = sourceMethodName
    copy.builder.append(builder.toString())
    copy.threadID = threadID
    copy.millis = millis
    copy.thrown = thrown
    copy.loggerName = loggerName
    copy.parameters = parameters
    copy.resourceBundle = resourceBundle
    copy.resourceBundleName = resourceBundleName

    copy.threadName = threadName
    copy.marker = marker
    copy.callLocation = callLocation
    copy.parameterCount = parameterCount
    return copy
  }

  override fun reset(): LogRecordBuilder {
    builder.setLength(0)
    return this
  }

  override fun append(str: String): LogRecordBuilder {
    builder.append(str)
    return this
  }

  override fun append(b: Boolean): LogRecordBuilder {
    builder.append(b)
    return this
  }

  override fun append(c: Char): LogRecordBuilder {
    builder.append(c)
    return this
  }

  override fun append(i: Int): LogRecordBuilder {
    builder.append(i)
    return this
  }

  override fun append(lng: Long): LogRecordBuilder {
    builder.append(lng)
    return this
  }

  override fun append(f: Float): LogRecordBuilder {
    builder.append(f)
    return this
  }

  override fun append(d: Double): LogRecordBuilder {
    builder.append(d)
    return this
  }

  override fun append(
    format: String,
    vararg args: Any
  ): LogRecordBuilder {
    return append(Locale.getDefault(), format, *args)
  }

  override fun append(
    locale: Locale,
    format: String,
    vararg args: Any
  ): LogRecordBuilder {
    if (args.isNotEmpty()) {
      formatter.format(locale, format, *args)
    } else {
      builder.append(format)
    }
    return this
  }

  override fun addLocation(stackDepth: Int): LogRecordBuilder {
    callLocation = LogUtil.getCallerLocation(stackDepth + 1)
    return this
  }

  override fun append(csq: CharSequence): Appendable {
    builder.append(csq)
    return this
  }

  override fun append(csq: CharSequence, start: Int, end: Int): Appendable {
    builder.append(csq, start, end)
    return this
  }

  companion object {
    private const val serialVersionUID = 936230097973648802L
    private val sequence = AtomicLong(1)
    private val threadLocalRecord = ThreadLocal<ExtLogRecord>()
    /** The default, and minimum, size of cached string builders. This is a per thread cost  */
    const val DEFAULT_STRING_BUILDER_SIZE = 1024
    /** The default maximum size of cached string builders  */
    @Suppress("MemberVisibilityCanBePrivate")
    const val DEFAULT_MAX_STRING_BUILDER_SIZE = 2048
    private var maxBuilderSize = DEFAULT_MAX_STRING_BUILDER_SIZE

    @Suppress("unused")
        /**
     * Sets the maximum size in bytes of the StringBuilder used to build log messages. If necessary,
     * a builder will be trimmed sometime after it's used but before it's is reused.
     * @param max the maximum size of the cached thread builder. Don't set this too high as you'll
     * this much memory for every thread that logs
     * @return the new max size which is maximum of DEFAULT_STRING_BUILDER_SIZE and max
     */
    fun setMaxStringBuilderSize(max: Int): Int {
      maxBuilderSize = Math.max(DEFAULT_STRING_BUILDER_SIZE, max)
      return maxBuilderSize
    }

    @Suppress("unused")
    /**
    *
    * @return the current maximum cached string builder size
    */
    fun getMaxStringBuilderSize(): Int {
      return maxBuilderSize
    }

    /**
     * Get a record and initialize it. Thread name and thread id will be set based on [Thread.currentThread]
     *
     *
     * Canonical use is:
     *
     * Java:
     * ```java
     * try (ExtLogRecord record = ExtLogRecord.get(...)) {
     * // use record here.
     * }
     * ```
     *
     * Kotlin:
     * ```kotlin
     * ExtLogRecord.get(...).use {
     * // use record here.
     * }
     * ```
     *
     * </pre>
     * Return the record via [.release] so new records don't need to be created for every log. Not releasing a record
     * defeats the pool. Preference is to use via try with resources as in example above.
     *
     * @return an ExtLogRecord initialized based on parameters and the current thread
     */
    fun get(
      level: LogLevel,
      msg: String,
      loggerName: String,
      callerLocation: StackTraceElement?,
      marker: Marker?,
      throwable: Throwable?,
      vararg formatArgs: Any
    ): ExtLogRecord {
      val logRecord =
        get(level, loggerName, marker, throwable)
      logRecord.message = msg
      logRecord.parameters = arrayOf(*formatArgs)
      if (callerLocation != null) {
        logRecord.sourceClassName = callerLocation.className
        logRecord.sourceMethodName = callerLocation.methodName
        logRecord.setLocation(callerLocation)
      }
      return logRecord
    }

    fun get(
      level: LogLevel,
      loggerName: String,
      marker: Marker?,
      throwable: Throwable?
    ): ExtLogRecord {
      val logRecord = record
      logRecord.logLevel = level
      logRecord.level = level.jdkLevel
      logRecord.setMarker(marker)
      logRecord.thrown = throwable
      val currentThread = Thread.currentThread()
      logRecord.threadName = currentThread.name
      logRecord.threadID = currentThread.id.toInt()
      logRecord.loggerName = loggerName
      return logRecord
    }

    fun release(record: ExtLogRecord) {
      record.release()
    }

    private val record: ExtLogRecord
      get() {
        var result: ExtLogRecord? = threadLocalRecord.get()
        if (result == null) {
          result = ExtLogRecord()
          threadLocalRecord.set(result)
        }
        return if (result.isReserved) ExtLogRecord().reserve() else result.reserve()
      }

    @TestOnly
    fun clearCachedRecord() {
      threadLocalRecord.set(null)
    }
  }

}
