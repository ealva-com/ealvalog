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
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.MarkerFactory;
import com.ealva.ealvalog.Markers;
import com.ealva.ealvalog.util.LogUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.util.IllegalFormatConversionException;
import java.util.Locale;

/**
 * Test LogRecord formatting
 * <p>
 * Created by Eric A. Snell on 3/5/17.
 */
public class ExtRecordFormatterTest {
  private static final LogLevel LOG_LEVEL = LogLevel.CRITICAL;
  private static final String MESSAGE_FORMAT = "%s";
  private static final String LOGGER_NAME = "LoggerName";
  private static final IllegalArgumentException THROWABLE = new IllegalArgumentException("Dummy");
  private static final Marker MARKER = Markers.INSTANCE.get("test");
  private static final String MESSAGE_ARG = "The Message";
  private static final String DECLARING_CLASS = "DeclaringClass";
  private static final String METHOD_NAME = "MethodName";
  private static final String FILE_NAME_JAVA = "FileName.java";
  private static final int LINE_NUMBER = 100;
  private static final String
      EXPECTED =
      String.format(Locale.getDefault(), "(%s:%d)", METHOD_NAME, LINE_NUMBER);
  private static final String
      EXPECTED_ALT =
      String.format(Locale.getDefault(), "(%s.%s:%d)", DECLARING_CLASS, METHOD_NAME, LINE_NUMBER);
  @SuppressWarnings("WeakerAccess") @Mock MarkerFactory markerFactory;
  private ExtLogRecord record;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    ExtLogRecord.Companion.clearCachedRecord();
    record = ExtLogRecord.Companion.get(LOG_LEVEL,
                                        MESSAGE_FORMAT,
                                        LOGGER_NAME,
                                        LogUtil.getCallerLocation(0),
                                        MARKER,
                                        THROWABLE,
                                        MESSAGE_ARG);
  }

  @After
  public void tearDown() {
    ExtLogRecord.Companion.release(record);
  }

  @Test(expected = IllegalFormatConversionException.class)
  public void testBadFormat() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%1$d");
    assertThat(formatter.format(record), is(equalTo(MESSAGE_ARG)));
  }

  /** Test that an error is returned in the format string instead of an exception being thrown */
  @Test
  public void testBadFormatLogException() {
    @SuppressWarnings("ThrowableNotThrown")
    IllegalFormatConversionException
        expected =
        new IllegalFormatConversionException('d', String.class);
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%1$d", true);
    assertThat(formatter.format(record), is(equalTo(expected.getMessage())));
  }

  @Test
  public void testMsgFormat() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%1$s");
    assertThat(formatter.format(record), is(equalTo(MESSAGE_ARG)));
    formatter.setFormat("%1$S");
    assertThat(formatter.format(record), is(equalTo(MESSAGE_ARG.toUpperCase())));
  }

  @Test
  public void testThreadId() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%2$d");
    final int threadID = 5000;
    record.setThreadID(threadID);
    assertThat(formatter.format(record), is(equalTo(Integer.toString(threadID))));
  }

  @Test
  public void testLoggerName() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%3$s");
    final String loggerName = "Rasputin";
    record.setLoggerName(loggerName);
    assertThat(formatter.format(record), is(equalTo(loggerName)));
  }

  @Test
  public void testLevel() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%4$s");
    assertThat(formatter.format(record), is(equalTo(LOG_LEVEL.toString())));
  }

  @Test
  public void testDate() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter =
        new com.ealva.ealvalog.core.ExtRecordFormatter(com.ealva.ealvalog.core.ExtRecordFormatter.DATE_TIME_FORMAT);
    final long MAR_8_2017_10_40_23_002 = 1488987623002L;
    record.setMillis(MAR_8_2017_10_40_23_002);
    assertThat(formatter.format(record), is(equalTo("2017-03-08 10:40:23.002")));
    formatter.setFormat(com.ealva.ealvalog.core.ExtRecordFormatter.DAY_DATE_TIME_FORMAT);
    assertThat(formatter.format(record), is(equalTo("Wed 2017-03-08 10:40:23.002")));
  }

  @Test
  public void testThrowable() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%6$s");
    assertThat(formatter.format(record), is(equalTo(THROWABLE.toString())));
  }

  @Test
  public void testThrowableAbsent() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%6$s");
    record.setThrown(null);
    assertThat(formatter.format(record), is(equalTo("")));
  }

  /**
   * I don't believe this test is fragile as the stack trace should always begin this class name. Testing further would be problematic
   * given we can't guarantee the test environment (works on my machine LOL).
   */
  @Test
  public void testThrowableAlternative() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%6$#s");
    // result will have Throwable.toString() followed by a line separator followed by the entire stack trace
    assertThat(formatter.format(record), is(startsWith(THROWABLE.toString() +
                                                           System.getProperty("line.separator") +
                                                           "\tat " +
                                                           getClass().getName())));
  }

  @Test
  public void testThrowableAbsentAlternative() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%6$#s");
    record.setThrown(null);
    assertThat(formatter.format(record), is(equalTo("")));
  }

  @Test
  public void testClassName() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%7$s");
    record.setSourceClassName(getClass().getName());
    assertThat(formatter.format(record), is(equalTo(getClass().getName())));
  }

  @Test
  public void testMethodName() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%8$s");
    final String methodName = "MethodName";
    record.setSourceMethodName(methodName);
    assertThat(formatter.format(record), is(equalTo(methodName)));
  }

  @Test
  public void testLocation() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%9$s");
    record.setLocation(new StackTraceElement(DECLARING_CLASS,
                                             METHOD_NAME,
                                             FILE_NAME_JAVA,
                                             LINE_NUMBER));
    assertThat(formatter.format(record), is(equalTo(EXPECTED)));
  }

  @Test
  public void testLocationAlt() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%9$#s");
    record.setLocation(new StackTraceElement(DECLARING_CLASS,
                                             METHOD_NAME,
                                             FILE_NAME_JAVA,
                                             LINE_NUMBER));
    assertThat(formatter.format(record), is(equalTo(EXPECTED_ALT)));
  }

  @Test
  public void testThreadName() {
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%10$s");
    final String threadName = "Athread-named-Bob";
    record.setThreadName(threadName);
    assertThat(formatter.format(record), is(equalTo(threadName)));
  }

  @Test
  public void testMarker() {
    com.ealva.ealvalog.core.ExtRecordFormatter formatter = new ExtRecordFormatter("%11$s");
    final String markerName = "MarkerName";
    final com.ealva.ealvalog.core.MarkerImpl marker = new MarkerImpl(markerName, markerFactory);
    record.setMarker(marker);
    assertThat(formatter.format(record), is(equalTo(markerName)));
  }

  /**
   * Test for a defect where the LogRecord message has already been properly formatted or
   * does not need to be formatted, yet contains formatting characters. The LogRecord parameters
   * of ExtLogRecord may be larger than the actual number of parameters and may also contain nulls.
   * ExtLogRecords maybe cached and the parameter array is reused so we don't have to allocate
   * arrays all the time. The formatter needs to check that the parameter array may be null or
   * contain nulls, making the actual parameter count smaller than the parameter array length.
   */
  @Test
  public void testNoParametersButHasFormatting() {
    record.close(); // release the fir
    record = ExtLogRecord.Companion.get(LOG_LEVEL,
                                        MESSAGE_FORMAT,
                                        LOGGER_NAME,
                                        LogUtil.getCallerLocation(0),
                                        MARKER,
                                        THROWABLE);
    com.ealva.ealvalog.core.ExtRecordFormatter
        formatter = new com.ealva.ealvalog.core.ExtRecordFormatter("%1$s");
    assertThat(formatter.format(record), is(equalTo(MESSAGE_FORMAT)));
  }
}
