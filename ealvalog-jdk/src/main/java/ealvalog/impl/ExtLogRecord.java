package ealvalog.impl;

import ealvalog.LogLevel;
import ealvalog.Marker;
import ealvalog.NullMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.logging.LogRecord;

/**
 * Subclass of LogRecord adding the extra info we need
 * <p>
 * Don't use the {@link #getParameters()} array length as the actual number of parameters. Use {@link #getParameterCount()} instead.
 * There might be nulls at the end of the array due to reuse
 * <p>
 * Created by Eric A. Snell on 3/4/17.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ExtLogRecord extends LogRecord {
  private final LogLevel logLevel;
  private @NotNull final String threadName;
  private @NotNull Marker marker;
  private int lineNumber;
  private int parameterCount;   // if we start pooling these we'll reuse the parameter count array

  public ExtLogRecord(@NotNull final LogLevel level, @NotNull final String msg) {
    super(level.getLevel(), msg);
    logLevel = level;
    final Thread thread = Thread.currentThread();
    threadName = thread.getName();
    marker = NullMarker.INSTANCE;
    lineNumber = 0;
    parameterCount = 0;
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

  /** @return the line number of the log call site, 0 if not available */
  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(final int lineNumber) {
    this.lineNumber = lineNumber;
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
}
