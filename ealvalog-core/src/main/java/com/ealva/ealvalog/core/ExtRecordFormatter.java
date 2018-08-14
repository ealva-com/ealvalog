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


import com.ealva.ealvalog.ExtLogRecord;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.util.FormattableStackTraceElement;
import com.ealva.ealvalog.util.FormattableThrowable;
import com.ealva.ealvalog.util.LogMessageFormatterImpl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IllegalFormatConversionException;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This formatter formats log record based on a String with {@link java.util.Formatter} style positional parameter.
 * <table cellpadding=5 summary="genConv">
 * <tr>
 * <th valign="bottom">Index
 * <th valign="bottom">Example
 * <th valign="bottom">Type
 * <th valign="bottom">Name
 * <tr>
 * <td align="center" valign="top">1$
 * <td align="center" valign="top">"%1$s"
 * <td align="center" valign="top">String
 * <td align="center" valign="top">Message
 * <p><tr>
 * <td align="center" valign="top">2$
 * <td align="center" valign="top">"%2$d"
 * <td align="center" valign="top">Integer
 * <td align="center" valign="top">Thread Id
 * <p><tr>
 * <td align="center" valign="top">3$
 * <td align="center" valign="top">"%3$s"
 * <td align="center" valign="top">String
 * <td align="center" valign="top">Logger Name
 * <p><tr>
 * <td align="center" valign="top">4$
 * <td align="center" valign="top">"%4$s"
 * <td align="center" valign="top">LogLevel
 * <td align="center" valign="top">Level
 * <p><tr>
 * <td align="center" valign="top">5$
 * <td align="center" valign="top">"%5$tF %5$tT.%5$tL"
 * <td align="center" valign="top">Long
 * <td align="center" valign="top">Date
 * <p><tr>
 * <td align="center" valign="top">6$
 * <td align="center" valign="top">"%6$s" or "%6$#s"
 * <td align="center" valign="top">FormattableThrowable
 * <td align="center" valign="top">Thrown <sup>1</sup>
 * <p><tr>
 * <td align="center" valign="top">7$
 * <td align="center" valign="top">"%7$s"
 * <td align="center" valign="top">String
 * <td align="center" valign="top">Class Name <sup>2</sup>
 * <p><tr>
 * <td align="center" valign="top">8$
 * <td align="center" valign="top">"%8$s"
 * <td align="center" valign="top">String
 * <td align="center" valign="top">Method Name <sup>2</sup>
 * <p><tr>
 * <td align="center" valign="top">9$
 * <td align="center" valign="top">"%9$s"
 * <td align="center" valign="top">FormattableStackTraceElement
 * <td align="center" valign="top">Location <sup>2</sup>
 * <p><tr>
 * <td align="center" valign="top">10$
 * <td align="center" valign="top">"%10$s"
 * <td align="center" valign="top">String
 * <td align="center" valign="top">Thread Name
 * <p><tr>
 * <td align="center" valign="top">11$
 * <td align="center" valign="top">"%11$s"
 * <td align="center" valign="top">Marker
 * <td align="center" valign="top">Marker
 * <p>
 * </table>
 * <p><sup>1</sup> # flag for stack trace
 * <p><sup>2</sup> Log call site
 * <p>
 * Created by Eric A. Snell on 3/4/17.
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnnecessaryBoxing"})
public class ExtRecordFormatter extends Formatter {
  /** Of the form: "2017-03-05 14:33:15.098" */
  public static final String DATE_TIME_FORMAT = "%5$tF %5$tT.%5$tL";
  /** Of the form: "Sun 2017-03-05 14:33:15.098" */
  public static final String DAY_DATE_TIME_FORMAT = "%5$ta " + DATE_TIME_FORMAT;

  private static final int MESSAGE_INDEX = 0;
  public static final String MESSAGE_POSITION = "%" + Integer.toString(MESSAGE_INDEX + 1);
  private static final int THREAD_ID_INDEX = 1;
  public static final String THREAD_ID_POSITION = "%" + Integer.toString(THREAD_ID_INDEX + 1);
  private static final int LOGGER_NAME_INDEX = 2;
  public static final String LOGGER_NAME_POSITION = "%" + Integer.toString(LOGGER_NAME_INDEX + 1);
  private static final int LOG_LEVEL_INDEX = 3;
  public static final String LOG_LEVEL_POSITION = "%" + Integer.toString(LOG_LEVEL_INDEX + 1);
  private static final int DATE_INDEX = 4;
  public static final String DATE_POSITION = "%" + Integer.toString(DATE_INDEX + 1);
  private static final int THROWN_INDEX = 5;
  public static final String THROWN_POSITION = "%" + Integer.toString(THROWN_INDEX + 1);
  private static final int CLASS_NAME_INDEX = 6;
  public static final String CLASS_NAME_POSITION = "%" + Integer.toString(CLASS_NAME_INDEX + 1);
  private static final int METHOD_NAME_INDEX = 7;
  public static final String METHOD_NAME_POSITION = "%" + Integer.toString(METHOD_NAME_INDEX + 1);
  private static final int LOCATION_INDEX = 8;
  public static final String LOCATION_POSITION = "%" + Integer.toString(LOCATION_INDEX + 1);
  private static final int THREAD_NAME_INDEX = 9;
  public static final String THREAD_NAME_POSITION = "%" + Integer.toString(THREAD_NAME_INDEX + 1);
  private static final int MARKER_INDEX = 10;
  public static final String MARKER_POSITION = "%" + Integer.toString(MARKER_INDEX + 1);

  public static final String MESSAGE_ARG = MESSAGE_POSITION + "$s";
  public static final String THREAD_ID_ARG = THREAD_ID_POSITION + "$d";
  public static final String LOGGER_NAME_ARG = LOGGER_NAME_POSITION + "$s";
  public static final String LOG_LEVEL_ARG = LOG_LEVEL_POSITION + "$s";
  public static final String DATE_ARG = DATE_TIME_FORMAT;
  public static final String THROWN_ARG = THROWN_POSITION + "$s";
  public static final String CLASS_NAME_ARG = CLASS_NAME_POSITION + "$s";
  public static final String METHOD_NAME_ARG = METHOD_NAME_POSITION + "$s";
  public static final String LOCATION_ARG = LOCATION_POSITION + "$s";
  public static final String THREAD_NAME_ARG = THREAD_NAME_POSITION + "$s";
  public static final String MARKER_ARG = MARKER_POSITION + "$s";
  public static final String THROWN_STACKTRACE_ARG = THROWN_POSITION + "$#s";
  public static final String LOCATION_WITH_CLASS_NAME_ARG = LOCATION_POSITION + "$#s";
  public static final String TYPICAL_FORMAT =
      DATE_TIME_FORMAT + " %4$s [%10$s] %3$s - %1$s %6$#s%n";
  public static final String TYPICAL_ANDROID_FORMAT = "[%10$s]%9$s %1$s";
  private static final ThreadLocal<ExtLogMessageFormatter> threadLocalFormatter =
      new ThreadLocal<ExtLogMessageFormatter>() {
        @Override
        protected ExtLogMessageFormatter initialValue() {
          return new ExtLogMessageFormatter();
        }

        @Override
        public ExtLogMessageFormatter get() {
          ExtLogMessageFormatter lmf = super.get();
          lmf.reset();
          return lmf;
        }
      };
  private static final int ARG_COUNT = 11;

  private @NotNull String format;
  private boolean logErrors;

  public ExtRecordFormatter() {
    this(TYPICAL_FORMAT, false);
  }

  public ExtRecordFormatter(final @NotNull String format) {
    this(format, false);
  }

  public ExtRecordFormatter(final @NotNull String format, final boolean logErrors) {
    this.format = format;
    this.logErrors = logErrors;
  }

  public boolean logErrors() {
    return logErrors;
  }

  public void setLogErrors(final boolean logErrors) {
    this.logErrors = logErrors;
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalFormatConversionException if either the log message format is bad or the
   *                                          ExtLogRecord format is bad. Not thrown if
   *                                          {@link #setLogErrors(boolean)} is true.
   * @see #logErrors
   * @see #setLogErrors(boolean)
   */
  @Override public String format(final LogRecord record) throws IllegalFormatConversionException {
    try {
      final ExtLogMessageFormatter formatter = threadLocalFormatter.get();
      final String msg = formatClientMessage(record, formatter);
      formatter.reset();
      if (record instanceof ExtLogRecord) {
        setArgs((ExtLogRecord)record, msg, formatter.formatterArgs);
      } else {
        setArgs(record, msg, formatter.formatterArgs);
      }
      return formatter.append(getFormat(), formatter.formatterArgs).toString();
    } catch (IllegalFormatConversionException e) {
      if (logErrors) {
        return e.getMessage();
      } else {
        throw e;
      }
    }
  }

  public @NotNull String getFormat() {
    return format;
  }

  public void setFormat(final @Nullable String format) {
    this.format = format == null ? TYPICAL_FORMAT : format;
  }

  protected String formatClientMessage(final LogRecord record,
                                       final ExtLogMessageFormatter formatter) {
    try {
      // Any logging client can send a LogRecord so make sure check the parameters
      final Object[] parameters = record.getParameters();
      int parameterCount = getParameterCount(parameters);
      if (parameterCount > 0) {
        return formatter.append(record.getMessage(), parameters).toString();
      } else {
        return formatter.append(record.getMessage()).toString();
      }
    } catch (IllegalFormatConversionException e) {
      if (logErrors) {
        return e.getMessage();
      } else {
        throw e;
      }
    }
  }

  /**
   * Return the actual number of parameters given the array may be null or contain nulls.
   *
   * @param parameters the parameters to be used for formatting, if any
   *
   * @return 0 if parameters is null, or the index of the first null item, or the length of
   * the parameters array if none are null.
   */
  private int getParameterCount(@Nullable final Object[] parameters) {
    if (null == parameters) { return 0; }
    for (int i = 0; i < parameters.length; i++) {
      if (null == parameters[i]) { return i; }
    }
    return parameters.length;
  }

  private void setArgs(final ExtLogRecord record,
                       final String msg,
                       final Object[] formatterArgs) {
    setBaseArgs(record, msg, formatterArgs);
    ((FormattableStackTraceElement)formatterArgs[LOCATION_INDEX]).setElement(record.getCallLocation());
    formatterArgs[THREAD_NAME_INDEX] = record.getThreadName();
    formatterArgs[MARKER_INDEX] = record.getMarker();
  }

  private void setBaseArgs(@NotNull final LogRecord record,
                           @NotNull final String message,
                           @NotNull final Object[] formatterArgs) {
    formatterArgs[MESSAGE_INDEX] = message;
    formatterArgs[THREAD_ID_INDEX] = Integer.valueOf(record.getThreadID());
    formatterArgs[LOGGER_NAME_INDEX] = record.getLoggerName();
    formatterArgs[LOG_LEVEL_INDEX] = LogLevel.Companion.fromLevel(record.getLevel());
    formatterArgs[DATE_INDEX] = Long.valueOf(record.getMillis());
    ((FormattableThrowable)formatterArgs[THROWN_INDEX]).setRealThrowable(record.getThrown());
    formatterArgs[CLASS_NAME_INDEX] = record.getSourceClassName();
    formatterArgs[METHOD_NAME_INDEX] = record.getSourceMethodName();
  }

  private void setArgs(final LogRecord record, final String msg, final Object[] formatterArgs) {
    setBaseArgs(record, msg, formatterArgs);
    ((FormattableStackTraceElement)formatterArgs[LOCATION_INDEX]).setElement(null);
    formatterArgs[THREAD_NAME_INDEX] = "";
    formatterArgs[MARKER_INDEX] = NullMarker.INSTANCE;
  }

  private static class ExtLogMessageFormatter extends LogMessageFormatterImpl {
    final Object[] formatterArgs = new Object[ARG_COUNT];

    ExtLogMessageFormatter() {
      // we want to prefill certain indices and then do sets
      formatterArgs[THROWN_INDEX] = FormattableThrowable.make(null);
      formatterArgs[LOCATION_INDEX] = FormattableStackTraceElement.make(null);
    }
  }
}
