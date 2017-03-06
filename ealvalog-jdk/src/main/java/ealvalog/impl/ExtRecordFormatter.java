package ealvalog.impl;


import ealvalog.NullMarker;
import ealvalog.util.LogMessageFormatter;
import ealvalog.util.NullThrowable;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This formatter formats log record based on a String with {@link java.util.Formatter} style positional parameter. In order these
 * parameters are (argument index - type - name):
 * <list>
 * <li>1$ - String - Message</li>
 * <li>2$ - Integer - Thread Id</li>
 * <li>3$ - String - Logger Name</li>
 * <li>4$ - LogLevel - Level</li>
 * <li>5$ - Long - Date</li>
 * <li>6$ - Throwable - Thrown</li>
 * <li>7$ - String - Class Name (log site)</li>
 * <li>8$ - String - Method Name (log site)</li>
 * <li>9$ - Integer - Line Number (log site)</li>
 * <li>10$ - String - Thread Name</li>
 * <li>11$ - Marker - Marker</li>
 * </list>
 * <p>
 * <p>
 * Created by Eric A. Snell on 3/4/17.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ExtRecordFormatter extends Formatter {
  private static final int MESSAGE_INDEX = 0;
  private static final int THREAD_ID_INDEX = 1;
  private static final int LOGGER_NAME_INDEX = 2;
  private static final int LEVEL_INDEX = 3;
  private static final int DATE_INDEX = 4;
  private static final int THROWN_INDEX = 5;
  private static final int CLASS_NAME_INDEX = 6;
  private static final int METHOD_NAME_INDEX = 7;
  private static final int LINE_NUMBER_INDEX = 8;
  private static final int THREAD_NAME_INDEX = 9;
  private static final int MARKER_INDEX = 10;

  public static final int MESSAGE_POSITION     = MESSAGE_INDEX + 1;
  public static final int THREAD_ID_POSITION   = THREAD_ID_INDEX + 1;
  public static final int LOGGER_NAME_POSITION = LOGGER_NAME_INDEX + 1;
  public static final int LEVEL_POSITION       = LEVEL_INDEX + 1;
  public static final int DATE_POSITION        = DATE_INDEX + 1;
  public static final int THROWN_POSITION      = THROWN_INDEX + 1;
  public static final int CLASS_NAME_POSITION  = CLASS_NAME_INDEX + 1;
  public static final int METHOD_NAME_POSITION = METHOD_NAME_INDEX + 1;
  public static final int LINE_NUMBER_POSITION = LINE_NUMBER_INDEX + 1;
  public static final int THREAD_NAME_POSITION = THREAD_NAME_INDEX + 1;
  public static final int MARKER_POSITION      = MARKER_INDEX + 1;

  /** Of the form: 2017-03-05 14:33:15.098 */
  public static final String DATE_TIME_FORMAT = "%5$ta %5$tF %5$tT.%5$tL";

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

  public ExtRecordFormatter(final String format) {
    setFormat(format);
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(final String format) {
    this.format = format == null ? "" : format;
  }

  @Override public String format(final LogRecord record) {
    final ExtLogMessageFormatter formatter = threadLocalFormatter.get();
    final String msg = formatter.format(record.getMessage(), record.getParameters()).toString();
    formatter.reset();
    if (record instanceof ExtLogRecord) {
      setArgs((ExtLogRecord)record, msg, formatter.formatterArgs);
    } else {
      setArgs(record, msg, formatter.formatterArgs);
    }
    return formatter.format(getFormat(), formatter.formatterArgs).toString();
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
    formatterArgs[LEVEL_INDEX] = record.getLevel();
    formatterArgs[DATE_INDEX] = Long.valueOf(record.getMillis());
    formatterArgs[THROWN_INDEX] = NullThrowable.nullToNullInstance(record.getThrown());
    formatterArgs[CLASS_NAME_INDEX] = record.getSourceClassName();
    formatterArgs[METHOD_NAME_INDEX] = record.getSourceMethodName();
  }

  private static class ExtLogMessageFormatter extends LogMessageFormatter {
    final Object[] formatterArgs = new Object[11];

  }
}
