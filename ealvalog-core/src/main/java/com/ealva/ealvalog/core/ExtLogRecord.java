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

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.util.LogMessageFormatter;
import com.ealva.ealvalog.util.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
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
 * to be {@link #release()}ed to be properly reused. Lower lowers of the logging framework
 * need to copy this ExtLogRecord if it's to be passed to another thread.
 * <p>
 * Don't use the {@link #getParameters()} array length as the actual number of parameters. Use {@link #getParameterCount()} instead.
 * There might be nulls at the end of the array due to reuse
 * <p>
 * Created by Eric A. Snell on 3/4/17.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ExtLogRecord extends LogRecord
    implements Closeable, // not AutoCloseable to be compatible with Android version < KitKat
               LogRecordBuilder {
  private static final long serialVersionUID = 936230097973648802L;
  private static final AtomicLong sequenceNumber = new AtomicLong(1);

  private LogLevel logLevel = LogLevel.NONE;
  private @NotNull String threadName = Thread.currentThread().getName();
  private @NotNull Marker marker = NullMarker.INSTANCE;
  private StackTraceElement location = null;
  private int parameterCount = 0;   // actual number of parameters, array might be over-sized
  private transient boolean reserved = false;
  private transient StringBuilder builder = null;
  private transient Formatter formatter = null;

  private static ThreadLocal<ExtLogRecord> threadLocalRecord = new ThreadLocal<>();

  private static ExtLogRecord getRecord() {
    ExtLogRecord result = threadLocalRecord.get();
    if (result == null) {
      result = new ExtLogRecord();
      threadLocalRecord.set(result);
    }
    return result.isReserved() ? new ExtLogRecord().reserve() : result.reserve();
  }

  /**
   * Get a record and initialize it. Thread name and thread id will be set based on {@link Thread#currentThread()}
   * <p>
   * Canonical use is:
   * <p>
   * <pre>
   * {@code
   * try (ExtLogRecord record = ExtLogRecord.get(...)) {
   *   // use record here.
   * }
   * }
   * </pre>
   * Return the record via {@link #release(ExtLogRecord)} so new records don't need to be created for every log. Not releasing a record
   * defeats the pool. Preference is to use via try with resources as in example above.
   *
   * @return an ExtLogRecord initialized based on parameters and the current thread
   */
  public static ExtLogRecord get(final @NotNull LogLevel level,
                                 final @NotNull String msg,
                                 final @NotNull String loggerName,
                                 final @Nullable StackTraceElement callerLocation,
                                 final @Nullable Throwable throwable,
                                 final @NotNull Object... formatArgs) {
    final ExtLogRecord logRecord = get(level, loggerName, null, throwable);
    logRecord.setMessage(msg);
    logRecord.setParameters(formatArgs);
    if (callerLocation != null) {
      logRecord.setSourceClassName(callerLocation.getClassName());
      logRecord.setSourceMethodName(callerLocation.getMethodName());
      logRecord.setLocation(callerLocation);
    }
    return logRecord;
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

  public static void release(final @NotNull ExtLogRecord record) {
    record.release();
  }

  private ExtLogRecord() {
    super(Level.OFF, "");
  }

  @NotNull
  private StringBuilder getBuilder() {
    if (builder == null) {
      builder = new StringBuilder(1024);
    }
    return builder;
  }

  @NotNull
  private Formatter getFormatter() {
    if (formatter == null) {
      formatter = new Formatter(getBuilder());
    }
    return formatter;
  }


  @Override public void setMessage(final String message) {
    StringBuilder builder = getBuilder();
    builder.setLength(0);
    builder.append(message);
  }

  @Override public String getMessage() {
    return getBuilder().toString();
  }

  private boolean isReserved() {
    return reserved;
  }

  private ExtLogRecord reserve() {
    reserved = true;
    logLevel = LogLevel.NONE;
    marker = NullMarker.INSTANCE;
    location = null;
    parameterCount = 0;
    setMillis(System.currentTimeMillis());
    setSequenceNumber(sequenceNumber.getAndIncrement());
    getBuilder().setLength(0);
    return this;
  }

  private void release() {
    reserved = false;
  }

  @NotNull
  public ExtLogRecord setLogLevel(@NotNull LogLevel level) {
    logLevel = level;
    setLevel(level.getJdkLevel());
    return this;
  }

  public LogLevel getLogLevel() {
    return logLevel;
  }

  /**
   * Name of the thread on which this instance was constructed
   *
   * @return thread name
   */
  @NotNull public String getThreadName() {
    return threadName;
  }

  /**
   * Returns the associated marker, which may be {@link NullMarker#INSTANCE} if no marker was set.
   *
   * @return the {@link Marker} set into this record, or {@link NullMarker#INSTANCE} if no contained marker
   */
  @NotNull public Marker getMarker() {
    return marker;
  }

  /** Set the marker, or clear it to {@link NullMarker#INSTANCE} if null is passed */
  public void setMarker(@Nullable final Marker marker) {
    this.marker = NullMarker.nullToNullInstance(marker);
  }

  public StackTraceElement getCallLocation() {
    return location;
  }

  public void setLocation(final @Nullable StackTraceElement location) {
    this.location = location;
  }

  /** @return the number of parameters passed to {@link #setParameters(Object[])} */
  public int getParameterCount() {
    return parameterCount;
  }

  /** If we start pooling this I want to already reuse the object array and keep track of actual parameter count */
  @Override public void setParameters(final Object[] parameters) {
    parameterCount = parameters.length;
    final Object[] existingParameters = getParameters();
    if (existingParameters != null && existingParameters.length >= parameters.length) {
      System.arraycopy(parameters, 0, existingParameters, 0, parameters.length);
      if (existingParameters.length > parameters.length) {
        Arrays.fill(existingParameters, parameters.length, existingParameters.length, null);
      }
      super.setParameters(existingParameters);  // in current impl this is redundant, but let's not assume LogRecord never changes
    } else {
      super.setParameters(parameters);
    }
  }

  public void setThreadName(@NotNull final String threadName) {
    this.threadName = threadName;
  }

  @Override public void close() {
    release(this);
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    logLevel = (LogLevel)in.readObject();
    threadName = in.readUTF();
    marker = (Marker)in.readObject();
    location = (StackTraceElement)in.readObject();
    parameterCount = in.readInt();
    reserved = false;
    builder = null;
    formatter = null;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(logLevel);
    out.writeUTF(threadName);
    out.writeObject(marker);
    out.writeObject(location);
    out.writeInt(parameterCount);
  }

  public ExtLogRecord copyOf() {
    ExtLogRecord copy = new ExtLogRecord();
    copy.setLogLevel(logLevel);  // handles Level and LogLevel
    copy.setSequenceNumber(getSequenceNumber());
    copy.setSourceClassName(getSourceClassName());
    copy.setSourceMethodName(getSourceMethodName());
    if (builder != null) {
      copy.builder = new StringBuilder(builder.toString());
    }
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
    return copy;
  }

  @NotNull @Override public LogRecordBuilder addLocation(final int stackDepth) {
    location = LogUtil.getCallerLocation(stackDepth + 1);
    return this;
  }

  @NotNull @Override public LogMessageFormatter reset() {
    getBuilder().setLength(0);
    return this;
  }

  @NotNull @Override public LogMessageFormatter append(@NotNull final String str) {
    getBuilder().append(str);
    return this;
  }

  @NotNull @Override public LogMessageFormatter append(boolean b) {
    getBuilder().append(b);
    return this;
  }

  @NotNull @Override public LogMessageFormatter append(int i) {
    getBuilder().append(i);
    return this;
  }

  @NotNull @Override public LogMessageFormatter append(long lng) {
    getBuilder().append(lng);
    return this;
  }

  @NotNull @Override public LogMessageFormatter append(float f) {
    getBuilder().append(f);
    return this;
  }

  @NotNull @Override public LogMessageFormatter append(double d) {
    getBuilder().append(d);
    return this;
  }

  @NotNull @Override public LogMessageFormatter append(@NotNull final String format,
                                                       @NotNull final Object... args) {
    return append(Locale.getDefault(), format, args);
  }

  @NotNull @Override public LogMessageFormatter append(@NotNull Locale locale,
                                                       @NotNull final String format,
                                                       @NotNull final Object... args) {
    if (args.length > 0) {
      getFormatter().format(locale, format, args);
    } else {
      getBuilder().append(format);
    }
    return this;
  }

  @Override public Appendable append(final CharSequence csq) {
    getBuilder().append(csq);
    return this;
  }

  @Override public Appendable append(final CharSequence csq, final int start, final int end) {
    getBuilder().append(csq, start, end);
    return this;
  }

  @NotNull @Override public LogMessageFormatter append(final char c) {
    getBuilder().append(c);
    return this;
  }

}
