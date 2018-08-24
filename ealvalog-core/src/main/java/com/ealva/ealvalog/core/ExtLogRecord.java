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

package com.ealva.ealvalog.core;

import com.ealva.ealvalog.LogEntry;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.util.LogUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Subclass of LogRecord adding the extra info we need. Not thread safe.
 * <p>
 * Use {@link #getRecord()} to obtain an ExtLogRecord which is associated with a thread and needs
 * to be {@link #close()}ed to be properly reused. Lower layers of the logging framework
 * need to copy this ExtLogRecord if it's to be passed to another thread.
 * <p>
 * Don't use the {@link #getParameters()} array length as the actual number of parameters. Use
 * {@link #getParameterCount()} instead. There might be nulls at the end of the array due to reuse
 * <p>
 * Created by Eric A. Snell on 3/4/17.
 */
@SuppressWarnings({"WeakerAccess"})
public class ExtLogRecord extends LogRecord implements LogEntry {
  private static final long serialVersionUID = 936230097973648802L;
  private static final AtomicLong sequenceNumber = new AtomicLong(1);
  private static ThreadLocal<ExtLogRecord> threadLocalRecord = new ThreadLocal<>();
  /** The default, and minimum, size of cached string builders. This is a per thread cost */
  public static final int DEFAULT_STRING_BUILDER_SIZE = 1024;
  /** The default maximum size of cached string builders */
  public static final int DEFAULT_MAX_STRING_BUILDER_SIZE = 2048;
  private static int maxBuilderSize = DEFAULT_MAX_STRING_BUILDER_SIZE;

  // Everything is transient as we will handle read/write during serialization
  private transient @NotNull LogLevel logLevel;
  private transient @NotNull String threadName;
  private transient @Nullable Marker marker;
  private transient @Nullable StackTraceElement location;
  private transient int parameterCount;   // actual number of parameters, array might be over-sized
  private transient int threadPriority;
  private transient long nanoTime;
  private transient boolean reserved;
  private transient @NotNull StringBuilder builder;
  private transient @NotNull Formatter formatter;

  /**
   * Sets the maximum size in bytes of the StringBuilder used to build log messages. If necessary,
   * a builder will be trimmed sometime after it's used but before it's is reused.
   *
   * @param max the maximum size of the cached thread builder. Don't set this too high as you'll
   *            this much memory for every thread that logs
   *
   * @return the new max size which is maximum of DEFAULT_STRING_BUILDER_SIZE and max
   */
  @SuppressWarnings("unused")
  public static int setMaxStringBuilderSize(final int max) {
    maxBuilderSize = Math.max(DEFAULT_STRING_BUILDER_SIZE, max);
    return maxBuilderSize;
  }

  /**
   * @return the current maximum cached string builder size
   */
  @SuppressWarnings("unused")
  public static int getMaxStringBuilderSize() {
    return maxBuilderSize;
  }

  public static ExtLogRecord get(final @NotNull LogLevel level,
                                 final @NotNull String loggerName,
                                 final @Nullable Marker marker,
                                 final @Nullable Throwable throwable) {
    final ExtLogRecord logRecord = getRecord();
    logRecord.logLevel = level;
    logRecord.setLevel(level.getJdkLevel());
    logRecord.setMarker(marker);
    logRecord.setThrown(throwable);
    final Thread currentThread = Thread.currentThread();
    logRecord.setThreadName(currentThread.getName());
    logRecord.setThreadID((int)currentThread.getId());
    logRecord.setLoggerName(loggerName);
    return logRecord;
  }

  private static ExtLogRecord getRecord() {
    ExtLogRecord result = threadLocalRecord.get();
    if (result == null) {
      result = new ExtLogRecord();
      threadLocalRecord.set(result);
    }
    return result.isReserved() ? new ExtLogRecord().reserve() : result.reserve();
  }

  /**
   * If entry is an ExtLogRecord it is returned, otherwise a new ExtLogRecord will be created.
   * Currently, new object creation is only necessary if someone sends a LogEntry into a Logger
   * from which it did not originate.
   *
   * @param entry the LogEntry, which should already be an ExtLogRecord
   *
   * @return entry if it is an ExtLogRecord or a new ExtLogRecord if entry must be converted
   */
  public static ExtLogRecord fromLogEntry(@NotNull final LogEntry entry) {
    if (ExtLogRecord.class.isAssignableFrom(entry.getClass())) {
      return (ExtLogRecord)entry;
    } else {
      entry.close();
      return new ExtLogRecord(entry);
    }
  }

  @TestOnly
  public static void clearCachedRecord() {
    threadLocalRecord.set(null);
  }

  @NotNull @Override public String getSourceClassName() {
    final String name = super.getSourceClassName();
    return name == null ? "" : name;
  }

  @NotNull @Override public String getSourceMethodName() {
    final String name = super.getSourceMethodName();
    return name == null ? "" : name;
  }

  @NotNull @Override public String getLoggerName() {
    final String name = super.getLoggerName();
    return name == null ? "Anonymous Logger" : name;
  }

  private ExtLogRecord() {
    super(Level.OFF, "");
    super.setParameters(new Object[]{null, null, null, null});
    parameterCount = 0;
    logLevel = LogLevel.NONE;
    threadName = Thread.currentThread().getName();
    builder = new StringBuilder(DEFAULT_STRING_BUILDER_SIZE);
    formatter = new Formatter(builder);
  }

  /**
   * This ctor should only be called if the logging framework client logs a LogEntry not
   * retrieved from the Logger which returns ExtLogRecord from getLogEntry()
   * @param entry entry to convert to an ExtLogRecord
   */
  private ExtLogRecord(@NotNull final LogEntry entry) {
    super(entry.getLogLevel().getJdkLevel(), entry.getMessage());
    parameterCount = 0;
    builder = new StringBuilder(DEFAULT_STRING_BUILDER_SIZE);
    formatter = new Formatter(builder);
    logLevel = entry.getLogLevel();
    setSequenceNumber(entry.getSequenceNumber());
    setSourceClassName(entry.getSourceClassName());
    setSourceMethodName(entry.getSourceMethodName());
    builder.append(entry.getMessage());
    setThreadID(entry.getThreadID());
    setMillis(entry.getMillis());
    setThrown(entry.getThrown());
    setLoggerName(entry.getLoggerName());
    threadName = entry.getThreadName();
    marker = entry.getMarker();
    location = entry.getLocation();
    threadPriority = entry.getThreadPriority();
    nanoTime = entry.getNanoTime();
  }

  private boolean isReserved() {
    return reserved;
  }

  private ExtLogRecord reserve() {
    reserved = true;
    super.setMessage(null);
    setParameters(null);
    logLevel = LogLevel.NONE;
    marker = null;
    location = null;
    setMillis(System.currentTimeMillis());
    setSequenceNumber(sequenceNumber.getAndIncrement());
    threadPriority = Thread.currentThread().getPriority();
    nanoTime = System.nanoTime();
    if (builder.capacity() > maxBuilderSize) {
      builder.setLength(maxBuilderSize);
      builder.trimToSize();
    }
    builder.setLength(0);
    return this;
  }

  public void setLocation(final @Nullable StackTraceElement location) {
    this.location = location;
  }


  @NotNull @Override public String getMessage() {
    return builder.toString();
  }

  @Override public void setMessage(final @Nullable String message) {
    StringBuilder builder = this.builder;
    builder.setLength(0);
    builder.append(message);
  }

  @Override public void setParameters(final @Nullable Object[] parameters) {
    final Object[] existingParameters = getParameters();
    if (parameters != null) {
      parameterCount = parameters.length;
      if (existingParameters != null && existingParameters.length >= parameters.length) {
        System.arraycopy(parameters, 0, existingParameters, 0, parameters.length);
        if (existingParameters.length > parameters.length) {
          Arrays.fill(existingParameters, parameters.length, existingParameters.length, null);
        }
        // in current impl this is redundant, but let's not assume LogRecord never changes
        super.setParameters(existingParameters);
      } else {
        super.setParameters(Arrays.copyOf(parameters, parameters.length));
      }
    } else {
      parameterCount = 0;
      if (existingParameters != null && existingParameters.length > 0) {
        Arrays.fill(existingParameters, null);
      }
    }
  }

  @Override public @NotNull LogLevel getLogLevel() {
    return logLevel;
  }

  @NotNull
  public ExtLogRecord setLogLevel(@NotNull LogLevel level) {
    logLevel = level;
    setLevel(level.getJdkLevel());
    return this;
  }

  /**
   * Name of the thread on which this instance was constructed
   *
   * @return thread name
   */
  @Override @NotNull public String getThreadName() {
    return threadName;
  }

  public void setThreadName(@NotNull final String threadName) {
    this.threadName = threadName;
  }

  /**
   * Returns the associated marker, which may be {@link NullMarker#INSTANCE} if no marker was set.
   *
   * @return the {@link Marker} set into this record, or {@link NullMarker#INSTANCE} if no contained marker
   */
  @Nullable public Marker getMarker() {
    return marker;
  }

  public void setMarker(@Nullable final Marker marker) {
    this.marker = marker;
  }

  public @Nullable StackTraceElement getLocation() {
    return location;
  }

  /** @return the number of parameters passed to {@link #setParameters(Object[])} */
  public int getParameterCount() {
    return parameterCount;
  }


  @Override public int getThreadPriority() {
    return threadPriority;
  }

  void setThreadPriority(int priority) {
    threadPriority = priority;
  }

  @Override public long getNanoTime() {
    return nanoTime;
  }

  void setNanoTime(long time) {
    nanoTime = time;
  }

  @Override public void close() {
    reserved = false;
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    logLevel = (LogLevel)in.readObject();
    threadName = in.readUTF();
    marker = (Marker)in.readObject();
    location = (StackTraceElement)in.readObject();
    parameterCount = in.readInt();
    threadPriority = in.readInt();
    nanoTime = in.readLong();
    reserved = false;
    builder = new StringBuilder(DEFAULT_STRING_BUILDER_SIZE);
    formatter = new Formatter(builder);
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(logLevel);
    out.writeUTF(threadName);
    out.writeObject(marker);
    out.writeObject(location);
    out.writeInt(parameterCount);
    out.writeInt(threadPriority);
    out.writeLong(nanoTime);
  }

  @SuppressWarnings("unused")
  public ExtLogRecord copyOf() {
    ExtLogRecord copy = new ExtLogRecord();
    copy.setLogLevel(logLevel);  // handles Level and LogLevel
    copy.setSequenceNumber(getSequenceNumber());
    copy.setSourceClassName(getSourceClassName());
    copy.setSourceMethodName(getSourceMethodName());
    copy.builder.append(builder.toString());
    copy.setThreadID(getThreadID());
    copy.setMillis(getMillis());
    copy.setThrown(getThrown());
    copy.setLoggerName(getLoggerName());
    copy.setParameters(getParameters());
    copy.setResourceBundle(getResourceBundle());
    copy.setResourceBundleName(getResourceBundleName());

    copy.threadName = threadName;
    copy.marker = marker;
    copy.location = location;
    copy.parameterCount = parameterCount;
    copy.threadPriority = threadPriority;
    copy.nanoTime = nanoTime;
    return copy;
  }

  @NotNull @Override public LogEntry reset() {
    builder.setLength(0);
    return this;
  }

  @NotNull @Override public LogEntry append(final char c) {
    builder.append(c);
    return this;
  }

  @NotNull @Override public LogEntry append(final CharSequence csq) {
    builder.append(csq);
    return this;
  }

  @NotNull @Override public LogEntry append(final CharSequence csq, final int start, final int end) {
    builder.append(csq, start, end);
    return this;
  }

  @NotNull @Override public LogEntry append(@NotNull final String str) {
    builder.append(str);
    return this;
  }

  @NotNull @Override public LogEntry append(boolean b) {
    builder.append(b);
    return this;
  }

  @NotNull @Override public LogEntry append(int i) {
    builder.append(i);
    return this;
  }

  @NotNull @Override public LogEntry append(long lng) {
    builder.append(lng);
    return this;
  }

  @NotNull @Override public LogEntry append(float f) {
    builder.append(f);
    return this;
  }

  @NotNull @Override public LogEntry append(double d) {
    builder.append(d);
    return this;
  }

  @NotNull @Override public LogEntry append(@NotNull final String format,
                                            @NotNull final Object... args) {
    return append(Locale.getDefault(), format, args);
  }

  @NotNull @Override public LogEntry append(@NotNull Locale locale,
                                            @NotNull final String format,
                                            @NotNull final Object... args) {
    if (args.length > 0) {
      formatter.format(locale, format, args);
    } else {
      builder.append(format);
    }
    return this;
  }

  @NotNull @Override public LogEntry addLocation(final int stackDepth) {
    location = LogUtil.getCallerLocation(stackDepth + 1);
    return this;
  }
}
