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

package ealvalog.impl;

import ealvalog.LogLevel;
import ealvalog.util.LogUtil;
import ealvalog.core.MarkerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.util.IllegalFormatConversionException;

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
  private static final String MESSAGE_ARG = "The Message";
  private ExtLogRecord record;

  @Before
  public void setup() {
    record = ExtLogRecord.get(LOG_LEVEL,
                              MESSAGE_FORMAT,
                              LOGGER_NAME,
                              LogUtil.getCallerLocation(0),
                              THROWABLE,
                              MESSAGE_ARG);
  }

  @After
  public void tearDown() {
    ExtLogRecord.release(record);
  }

  @Test(expected = IllegalFormatConversionException.class)
  public void testBadFormat() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%1$d");
    assertThat(formatter.format(record), is(equalTo(MESSAGE_ARG)));
  }

  /** Test that an error is returned in the format string instead of an exception being thrown */
  @Test
  public void testBadFormatLogException() {
    @SuppressWarnings("ThrowableNotThrown")
    IllegalFormatConversionException expected = new IllegalFormatConversionException('d', String.class);
    ExtRecordFormatter formatter = new ExtRecordFormatter("%1$d", true);
    assertThat(formatter.format(record), is(equalTo(expected.getMessage())));
  }

  @Test
  public void testMsgFormat() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%1$s");
    assertThat(formatter.format(record), is(equalTo(MESSAGE_ARG)));
    formatter.setFormat("%1$S");
    assertThat(formatter.format(record), is(equalTo(MESSAGE_ARG.toUpperCase())));
  }

  @Test
  public void testThreadId() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%2$d");
    final int threadID = 5000;
    record.setThreadID(threadID);
    assertThat(formatter.format(record), is(equalTo(Integer.toString(threadID))));
  }

  @Test
  public void testLoggerName() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%3$s");
    final String loggerName = "Rasputin";
    record.setLoggerName(loggerName);
    assertThat(formatter.format(record), is(equalTo(loggerName)));
  }

  @Test
  public void testLevel() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%4$s");
    assertThat(formatter.format(record), is(equalTo(LOG_LEVEL.toString())));
  }

  @Test
  public void testDate() {
    ExtRecordFormatter formatter = new ExtRecordFormatter(ExtRecordFormatter.DATE_TIME_FORMAT);
    final long MAR_8_2017_10_40_23_002 = 1488987623002L;
    record.setMillis(MAR_8_2017_10_40_23_002);
    assertThat(formatter.format(record), is(equalTo("2017-03-08 10:40:23.002")));
    formatter.setFormat(ExtRecordFormatter.DAY_DATE_TIME_FORMAT);
    assertThat(formatter.format(record), is(equalTo("Wed 2017-03-08 10:40:23.002")));
  }

  @Test
  public void testThrowable() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%6$s");
    assertThat(formatter.format(record), is(equalTo(THROWABLE.toString())));
  }

  @Test
  public void testThrowableAbsent() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%6$s");
    record.setThrown(null);
    assertThat(formatter.format(record), is(equalTo("")));
  }

  /**
   * I don't believe this test is fragile as the stack trace should always begin this class name. Testing further would be problematic
   * given we can't guarantee the test environment (works on my machine LOL).
   */
  @Test
  public void testThrowableAlternative() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%6$#s");
    // result will have Throwable.toString() followed by a line separator followed by the entire stack trace
    assertThat(formatter.format(record), is(startsWith(THROWABLE.toString() +
                                                           System.getProperty("line.separator") +
                                                           "\tat " +
                                                           getClass().getName())));
  }

  @Test
  public void testThrowableAbsentAlternative() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%6$#s");
    record.setThrown(null);
    assertThat(formatter.format(record), is(equalTo("")));
  }

  @Test
  public void testClassName() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%7$s");
    record.setSourceClassName(getClass().getName());
    assertThat(formatter.format(record), is(equalTo(getClass().getName())));
  }

  @Test
  public void testMethodName() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%8$s");
    final String methodName = "MethodName";
    record.setSourceMethodName(methodName);
    assertThat(formatter.format(record), is(equalTo(methodName)));
  }

  @Test
  public void testLineNumber() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%9$d");
    final int lineNumber = 101;
    record.setLineNumber(lineNumber);
    assertThat(formatter.format(record), is(equalTo(Integer.toString(lineNumber))));
  }

  @Test
  public void testThreadName() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%10$s");
    final String threadName = "Athread-named-Bob";
    record.setThreadName(threadName);
    assertThat(formatter.format(record), is(equalTo(threadName)));
  }

  @Test
  public void testMarker() {
    ExtRecordFormatter formatter = new ExtRecordFormatter("%11$s");
    final String markerName = "MarkerName";
    final MarkerImpl marker = new MarkerImpl(markerName);
    record.setMarker(marker);
    assertThat(formatter.format(record), is(equalTo(markerName)));
  }

}
