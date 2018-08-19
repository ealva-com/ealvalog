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

package com.ealva.ealvalog.java;

import com.ealva.ealvalog.ExtLogRecord;
import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.util.NullThrowable;

import static com.ealva.ealvalog.LogLevel.CRITICAL;
import static com.ealva.ealvalog.LogLevel.ERROR;
import static com.ealva.ealvalog.LogLevel.INFO;
import static com.ealva.ealvalog.LogLevel.WARN;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Eric A. Snell on 8/13/18.
 */
public class JLoggerImplTest {
  private static final String MSG = "TheMessage";

  @Mock Logger realLogger;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void isLoggable() {
    when(realLogger.isLoggable(INFO, null, null)).thenReturn(false);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, "dummy");
    assertThat(logger.isLoggable(INFO), is(false));
    then(realLogger).should(times(2)).isLoggable(INFO, null, null);
    then(realLogger).should(never()).logImmediate(any(ExtLogRecord.class));
  }

  @Test
  public void isLoggableMarker() {
    when(realLogger.getMarker()).thenReturn(NullMarker.INSTANCE);
    when(realLogger.isLoggable(INFO, NullMarker.INSTANCE, null)).thenReturn(false);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, "dummy");
    assertThat(logger.isLoggable(INFO, NullMarker.INSTANCE), is(false));
    then(realLogger).should(times(2)).isLoggable(INFO, NullMarker.INSTANCE, null);
    then(realLogger).should(never()).logImmediate(any(ExtLogRecord.class));
  }

  @Test
  public void isLoggableThrowable() {
    when(realLogger.isLoggable(INFO, null, NullThrowable.INSTANCE)).thenReturn(false);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, NullThrowable.INSTANCE, "dummy");
    assertThat(logger.isLoggable(INFO, NullThrowable.INSTANCE), is(false));
    then(realLogger).should(times(2)).isLoggable(INFO, null, NullThrowable.INSTANCE);
    then(realLogger).should(never()).logImmediate(any(ExtLogRecord.class));
  }

  @Test
  public void isLoggableMarkerAndThrowable() {
    when(realLogger.isLoggable(INFO,
                               NullMarker.INSTANCE,
                               NullThrowable.INSTANCE)).thenReturn(false);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, NullMarker.INSTANCE, NullThrowable.INSTANCE, "dummy");
    assertThat(logger.isLoggable(INFO, NullMarker.INSTANCE, NullThrowable.INSTANCE), is(false));
    then(realLogger).should(times(2)).isLoggable(INFO, NullMarker.INSTANCE, NullThrowable.INSTANCE);
    then(realLogger).should(never()).logImmediate(any(ExtLogRecord.class));
  }

  @Test
  public void isLoggableImplicitMarkerAndThrowable() {
    when(realLogger.getMarker()).thenReturn(NullMarker.INSTANCE);
    when(realLogger.isLoggable(INFO,
                               NullMarker.INSTANCE,
                               NullThrowable.INSTANCE)).thenReturn(false);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, NullThrowable.INSTANCE, "dummy");
    then(realLogger).should(times(1)).isLoggable(INFO, NullMarker.INSTANCE, NullThrowable.INSTANCE);
    then(realLogger).should(never()).logImmediate(any(ExtLogRecord.class));
  }

  @Test
  public void logMarkerMessage() {
    when(realLogger.isLoggable(WARN, NullMarker.INSTANCE, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(WARN, NullMarker.INSTANCE, MSG);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(WARN.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(MSG)));
  }

  @Test
  public void logMarkerFormatMessage() {
    when(realLogger.isLoggable(WARN, NullMarker.INSTANCE, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(WARN, NullMarker.INSTANCE, "%s%d", "one", 2);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(WARN.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo("one2")));
  }

  @Test
  public void logThrowableFormatMessage() {
    when(realLogger.isLoggable(ERROR, null, NullThrowable.INSTANCE)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(ERROR, NullThrowable.INSTANCE, "%d%s", 1, "two");

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(ERROR.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo("1two")));
  }

  @Test
  public void logMarkerThrowableFormatMessage() {
    when(realLogger.isLoggable(CRITICAL, NullMarker.INSTANCE, NullThrowable.INSTANCE)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(CRITICAL, NullMarker.INSTANCE, NullThrowable.INSTANCE, "%d%S", 1, "two");

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(CRITICAL.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo("1TWO")));
  }

  private final static String one = "one";
  private final static String two = "two";
  private final static String three = "three";
  private final static String four = "four";
  private final static String five = "five";
  private final static String six = "six";

  @Test
  public void logOneArg() {
    when(realLogger.isLoggable(INFO, null, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, "%s", one);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(INFO.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(one)));
  }

  @Test
  public void logTwoArgs() {
    when(realLogger.isLoggable(INFO, null, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, "%s%s", one, two);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(INFO.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(one+two)));
  }

  @Test
  public void logThreeArgs() {
    when(realLogger.isLoggable(INFO, null, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, "%s%s%s", one, two, three);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(INFO.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(one+two+three)));
  }

  @Test
  public void logFourArgs() {
    when(realLogger.isLoggable(INFO, null, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, "%s%s%s%s", one, two, three, four);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(INFO.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(one+two+three+four)));
  }

  @Test
  public void logMoreThan4Args() {
    when(realLogger.isLoggable(INFO, null, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(INFO, "%s%s%s%s%s%s", one, two, three, four, five, six);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(INFO.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(one + two + three + four + five + six)));
  }

  @Test
  public void logSupplier() {
    when(realLogger.isLoggable(WARN, null, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(WARN, () -> MSG);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(WARN.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(MSG)));
  }

  @Test
  public void logMarkerSupplier() {
    when(realLogger.isLoggable(WARN, NullMarker.INSTANCE, null)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(WARN, NullMarker.INSTANCE, () -> new StringBuilder(MSG));

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(WARN.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(MSG)));
    assertThat(record.getMarker(), is(NullMarker.INSTANCE));
    assertThat(record.getThrown(), is(nullValue()));
  }

  @Test
  public void logThrowableSupplier() {
    when(realLogger.isLoggable(WARN, null, NullThrowable.INSTANCE)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(WARN, NullThrowable.INSTANCE, () -> MSG);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(WARN.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(MSG)));
    assertThat(record.getMarker(), is(nullValue()));
    assertThat(record.getThrown(), is(NullThrowable.INSTANCE));
  }

  @Test
  public void logMarkerThrowableSupplier() {
    when(realLogger.isLoggable(WARN, NullMarker.INSTANCE, NullThrowable.INSTANCE)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(WARN, NullMarker.INSTANCE, NullThrowable.INSTANCE, () -> new RuntimeException("Test"));

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(WARN.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo("java.lang.RuntimeException: Test")));
    assertThat(record.getMarker(), is(NullMarker.INSTANCE));
    assertThat(record.getThrown(), is(NullThrowable.INSTANCE));
  }

  @Test
  public void logCaught() {
    RuntimeException ex = new RuntimeException("MeMeMe");
    when(realLogger.isLoggable(INFO, null, ex)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.caught(INFO, ex);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(INFO.getJdkLevel()));
    assertThat(record.getThrown(), is(ex));
  }

  @Test
  public void logThrowing() {
    RuntimeException ex = new RuntimeException("MeMeMe");
    when(realLogger.isLoggable(INFO, null, ex)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    Throwable returned = logger.throwing(INFO, ex);

    assertThat(returned, is(ex));
    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(INFO.getJdkLevel()));
    assertThat(record.getThrown(), is(ex));
  }


}