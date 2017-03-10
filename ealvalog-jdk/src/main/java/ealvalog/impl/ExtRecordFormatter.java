package ealvalog.impl;


import ealvalog.LogLevel;
import ealvalog.NullMarker;
import ealvalog.util.LogMessageFormatter;
import ealvalog.util.NullThrowable;
import org.jetbrains.annotations.NotNull;

import java.util.IllegalFormatConversionException;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This formatter formats log record based on a String with {@link java.util.Formatter} style positional parameter. In order these
 * parameters are (argument index - example - type - name ):
 * <p>
 * <list>
 * <li>1$ - "%1$s" - String - Message</li>
 * <li>2$ - "%2$d" - Integer - Thread Id</li>
 * <li>3$ - "%3$s" - String - Logger Name</li>
 * <li>4$ - "%4$s" - LogLevel - Level</li>
 * <li>5$ - "%5$tF %5$tT.%5$tL" - Long - Date</li>
 * <li>6$ - "%6$s" or "%6$#s" - FormattableThrowable - Thrown (use alternative to get stack trace - # flag)</li>
 * <li>7$ - "%7$s" - String - Class Name (log site)</li>
 * <li>8$ - "%8$s" - String - Method Name (log site)</li>
 * <li>9$ - "%9$d" - Integer - Line Number (log site)</li>
 * <li>10$ - "%10$s" String - Thread Name</li>
 * <li>11$ - "%11$s"Marker - Marker</li>
 * </list>
 * <p>
 * Created by Eric A. Snell on 3/4/17.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ExtRecordFormatter extends Formatter {
  private static final int MESSAGE_INDEX = 0;
  private static final int THREAD_ID_INDEX = 1;
  private static final int LOGGER_NAME_INDEX = 2;
  private static final int LOG_LEVEL_INDEX = 3;
  private static final int DATE_INDEX = 4;
  private static final int THROWN_INDEX = 5;
  private static final int CLASS_NAME_INDEX = 6;
  private static final int METHOD_NAME_INDEX = 7;
  private static final int LINE_NUMBER_INDEX = 8;
  private static final int THREAD_NAME_INDEX = 9;
  private static final int MARKER_INDEX = 10;

  public static final int MESSAGE_POSITION = MESSAGE_INDEX + 1;
  public static final int THREAD_ID_POSITION = THREAD_ID_INDEX + 1;
  public static final int LOGGER_NAME_POSITION = LOGGER_NAME_INDEX + 1;
  public static final int LOG_LEVEL_POSITION = LOG_LEVEL_INDEX + 1;
  public static final int DATE_POSITION = DATE_INDEX + 1;
  public static final int THROWN_POSITION = THROWN_INDEX + 1;
  public static final int CLASS_NAME_POSITION = CLASS_NAME_INDEX + 1;
  public static final int METHOD_NAME_POSITION = METHOD_NAME_INDEX + 1;
  public static final int LINE_NUMBER_POSITION = LINE_NUMBER_INDEX + 1;
  public static final int THREAD_NAME_POSITION = THREAD_NAME_INDEX + 1;
  public static final int MARKER_POSITION = MARKER_INDEX + 1;

  /** Of the form: "2017-03-05 14:33:15.098" */
  public static final String DATE_TIME_FORMAT = "%5$tF %5$tT.%5$tL";

  /** Of the form: "Sun 2017-03-05 14:33:15.098" */
  public static final String DAY_DATE_TIME_FORMAT = "%5$ta " + DATE_TIME_FORMAT;

  public static final String TYPICAL_FORMAT = DATE_TIME_FORMAT + " %4$s [%10$s] %3$s - %1$s %6$#s";

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

  private String format;
  private boolean logErrors;

  public ExtRecordFormatter() {
    format = TYPICAL_FORMAT;
    logErrors = false;
  }

  public ExtRecordFormatter(final @NotNull String format) {
    this(format, false);
  }

  public ExtRecordFormatter(final @NotNull String format, final boolean logErrors) {
    setFormat(format);
    this.logErrors = logErrors;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(final String format) {
    this.format = format == null ? TYPICAL_FORMAT : format;
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
   * @throws IllegalFormatConversionException if either the log message format is bad or the ExtLogRecord format is bad. Not thrown if
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
      return formatter.format(getFormat(), formatter.formatterArgs).toString();
    } catch (Exception e) {
      if (logErrors) {
        return e.getMessage();
      } else {
        throw e;
      }
    }
  }

  protected String formatClientMessage(final LogRecord record, final ExtLogMessageFormatter formatter) {
    try {
      return formatter.format(record.getMessage(), record.getParameters()).toString();
    } catch (Exception e) {
      if (logErrors) {
        return e.getMessage();
      } else {
        throw e;
      }
    }
  }

  @SuppressWarnings("UnnecessaryBoxing")
  private void setArgs(final ExtLogRecord record, final String msg, final Object[] formatterArgs) {
    setBaseArgs(record, msg, formatterArgs);
    formatterArgs[LINE_NUMBER_INDEX] = Integer.valueOf(record.getLineNumber());
    formatterArgs[THREAD_NAME_INDEX] = record.getThreadName();
    formatterArgs[MARKER_INDEX] = record.getMarker();
  }

  @SuppressWarnings("UnnecessaryBoxing")
  private void setArgs(final LogRecord record, final String msg, final Object[] formatterArgs) {
    setBaseArgs(record, msg, formatterArgs);
    formatterArgs[LINE_NUMBER_INDEX] = Integer.valueOf(0);
    formatterArgs[THREAD_NAME_INDEX] = "";
    formatterArgs[MARKER_INDEX] = NullMarker.INSTANCE;
  }

  @SuppressWarnings("UnnecessaryBoxing")
  private void setBaseArgs(@NotNull final LogRecord record, @NotNull final String message, @NotNull final Object[] formatterArgs) {
    formatterArgs[MESSAGE_INDEX] = message;
    formatterArgs[THREAD_ID_INDEX] = Integer.valueOf(record.getThreadID());
    formatterArgs[LOGGER_NAME_INDEX] = record.getLoggerName();
    formatterArgs[LOG_LEVEL_INDEX] = LogLevel.fromLevel(record.getLevel());
    formatterArgs[DATE_INDEX] = Long.valueOf(record.getMillis());
    formatterArgs[THROWN_INDEX] = NullThrowable.nullToNullInstance(record.getThrown());
    formatterArgs[CLASS_NAME_INDEX] = record.getSourceClassName();
    formatterArgs[METHOD_NAME_INDEX] = record.getSourceMethodName();
  }

  private static class ExtLogMessageFormatter extends LogMessageFormatter {
    final Object[] formatterArgs = new Object[11];

  }
}
