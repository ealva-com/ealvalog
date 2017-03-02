/*
 * Copyright 2017 Eric A. Snell
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

package ealvalog.base;

import ealvalog.Level;
import ealvalog.Logger;
import ealvalog.Marker;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BaseLogger provides MessageFormatter, consolidates various logging methods to one method after checking log level and obtaining
 * optional info such as log() invocation site.
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
public abstract class BaseLogger implements Logger {
  private static final int STACK_DEPTH = 1;
  private static final Object[] EMPTY_ARRAY = new Object[0];

  private static final ThreadLocal<LogMessageFormatter> threadLocalFormatter =
      new ThreadLocal<LogMessageFormatter>() {
        @Override
        protected LogMessageFormatter initialValue() {
          return new LogMessageFormatter();
        }

        @Override
        public LogMessageFormatter get() {
          LogMessageFormatter lmf = super.get();
          lmf.reset();
          return lmf;
        }
      };


  private Marker marker;

  protected BaseLogger() {
    this.marker = null;
  }

  protected BaseLogger(@Nullable final Marker marker) {
    this.marker = marker;
  }

  @Override public Marker getMarker() {
    return marker;
  }

  @Override public void setMarker(final Marker marker) {
    this.marker = marker;
  }

  @Override public boolean isLoggable(@NotNull final Level level) {
    return isLoggable(level, null);
  }

  @Override public void log(@NotNull final Level level, @NotNull final String msg) {
    if (isLoggable(level)) {
      doLog(level, marker, null, getCallSite(STACK_DEPTH, level, marker, null), threadLocalFormatter.get(), msg, EMPTY_ARRAY);
    }
  }

  @Override public void log(@NotNull final Level level, @NotNull final Marker marker, @NotNull final String msg) {
    if (isLoggable(level, marker)) {
      doLog(level, marker, null, getCallSite(STACK_DEPTH, level, marker, null), threadLocalFormatter.get(), msg, EMPTY_ARRAY);
    }
  }

  @Override public void log(@NotNull final Level level, @NotNull final Throwable throwable, @NotNull final String msg) {
    if (isLoggable(level)) {
      doLog(level, marker, throwable, getCallSite(STACK_DEPTH, level, marker, throwable), threadLocalFormatter.get(), msg, EMPTY_ARRAY);
    }
  }

  @Override
  public void log(@NotNull final Level level, @NotNull final Marker marker, @NotNull final Throwable throwable, @NotNull final String msg) {
    if (isLoggable(level, marker)) {
      doLog(level, marker, throwable, getCallSite(STACK_DEPTH, level, marker, throwable), threadLocalFormatter.get(), msg, EMPTY_ARRAY);
    }
  }

  @Override public void log(@NotNull final Level level, @NotNull final String format, @NotNull final Object... formatArgs) {
    if (isLoggable(level)) {
      doLog(level, marker, null, getCallSite(STACK_DEPTH, level, marker, null), threadLocalFormatter.get(), format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final Level level,
                  @NotNull final Marker marker,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level, marker)) {
      doLog(level, marker, null, getCallSite(STACK_DEPTH, level, marker, null), threadLocalFormatter.get(), format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final Level level,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  @NotNull final Object... formatArgs) {
    if (isLoggable(level)) {
      doLog(level, marker, throwable, getCallSite(STACK_DEPTH, level, marker, throwable), threadLocalFormatter.get(), format, formatArgs);
    }
  }

  @Override
  public void log(@NotNull final Level level,
                  @NotNull final Marker marker,
                  @NotNull final Throwable throwable,
                  @NotNull final String format,
                  final @NotNull Object[] formatArgs) {

  }

  @Override public void log(@NotNull final Level level,
                            @NotNull final String format,
                            @NotNull final Object arg1) {
    if (isLoggable(level, marker)) {
      doLog(level, marker, null, getCallSite(STACK_DEPTH, level, marker, null), threadLocalFormatter.get(), format, arg1);
    }
  }

  @Override public void log(@NotNull final Level level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2) {
    if (isLoggable(level, marker)) {
      doLog(level, marker, null, getCallSite(STACK_DEPTH, level, marker, null), threadLocalFormatter.get(), format, arg1, arg2);
    }
  }

  @Override public void log(@NotNull final Level level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3) {
    if (isLoggable(level, marker)) {
      doLog(level, marker, null, getCallSite(STACK_DEPTH, level, marker, null), threadLocalFormatter.get(), format, arg1, arg2, arg3);
    }
  }

  @Override public void log(@NotNull final Level level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3,
                            @NotNull final Object arg4) {
    if (isLoggable(level, marker)) {
      doLog(level, marker, null, getCallSite(STACK_DEPTH, level, marker, null), threadLocalFormatter.get(), format, arg1, arg2, arg3, arg4);
    }
  }

  @Override public void log(@NotNull final Level level,
                            @NotNull final String format,
                            @NotNull final Object arg1,
                            @NotNull final Object arg2,
                            @NotNull final Object arg3,
                            @NotNull final Object arg4,
                            @NotNull final Object... remaining) {
    if (isLoggable(level, marker)) {
      doLog(level,
            marker,
            null,
            getCallSite(STACK_DEPTH, level, marker, null),
            threadLocalFormatter.get(),
            format,
            combineArgs(remaining, arg1, arg2, arg3, arg4));
    }
  }

  /**
   * All logging funnels through here. Subclasses implement this method to perform the actual concrete logging.
   * <p>
   * Note: even though a particular implementation of this class returns false from {@link #shouldIncludeLocation(Level, Marker,
   * Throwable)}, this method might still receive a valid {@link StackTraceElement} instance if another part of the framework required the
   * information. So though unexpected, implementations may still wish to make use of the information if available.
   *
   * @param level          the log level
   * @param marker         an optional {@link Marker}
   * @param throwable      an optional {@link Throwable}
   * @param callerLocation optional call site information
   * @param formatter      the formatter to be used  during this invocation (do not retain this instance as it will be reused)
   * @param msg            the message or format string passed by the client
   * @param formatArgs     any format arguments passed by the client. Never null but may be zero length if no formatting is necessary
   */
  protected abstract void doLog(@NotNull final Level level,
                                @Nullable final Marker marker,
                                @Nullable final Throwable throwable,
                                @Nullable final StackTraceElement callerLocation,
                                @NotNull final LogMessageFormatter formatter,
                                @NotNull final String msg,
                                @NotNull final Object... formatArgs);

  /**
   * Does this Logger implementation want information about where the log method was invoked. This may be a relatively expensive operation.
   * If a Throwable is included in the log information it might be enough information, though the throw site may drastically differ from
   * the log site.
   *
   * @return true if the log information should include a {@link StackTraceElement} indicating the call site of the log invocation.
   */
  protected abstract boolean shouldIncludeLocation(@NotNull Level level, @Nullable Marker marker, final Throwable throwable);

  private StackTraceElement getCallSite(final int currentStackDepthFromCallSite,
                                        final Level level,
                                        final Marker marker,
                                        final Throwable throwable) {
    if (shouldIncludeLocation(level, marker, throwable)) {
      LogUtil.getCallerLocation(currentStackDepthFromCallSite + 1);
    }
    return null;
  }

  /**
   * Make an Object[] from 2 original arrays. The first array was passed in by the client as original varargs. The second, our internal
   * varargs, should actually precede the original as they were listed first in the parameter list by the client.
   *
   * @param formatArgs objects that come LAST in the resulting array
   * @param preceding  objects that come first, specified as varargs as a nicety
   *
   * @return joined array of objects, {@code preceding} first, followed by {@code formatArgs}
   */
  @NotNull private Object[] combineArgs(@NotNull final Object[] formatArgs, @NotNull Object... preceding) {
    Object[] result = new Object[formatArgs.length + preceding.length];
    System.arraycopy(preceding, 0, result, 0, preceding.length);
    System.arraycopy(formatArgs, 0, result, preceding.length, formatArgs.length);
    return result;
  }

  @NotNull private Object[] convertToObjects(@NotNull Object... primitivesOrObjects) {
    return primitivesOrObjects;
  }

}
