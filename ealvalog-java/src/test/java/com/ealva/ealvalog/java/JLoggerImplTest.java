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

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.core.ExtLogRecord;
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
  private static final String LOGGER_NAME = "LoggerName";
  private static final String LOGGER_FQCN = "com.acme.loggers.TheLogger";
  @Mock Logger realLogger;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(realLogger.getName()).thenReturn(LOGGER_NAME);
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
    LogLevel level = ERROR;
    Marker marker = NullMarker.INSTANCE;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, marker, MSG);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(MSG)));
  }

  @Test
  public void logMarkerFormatMessage() {
    LogLevel level = WARN;
    Marker marker = NullMarker.INSTANCE;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, marker, "%s%d", "one", 2);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
  }

  @Test
  public void logThrowableFormatMessage() {
    LogLevel level = ERROR;
    Marker marker = null;
    Throwable throwable = NullThrowable.INSTANCE;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, throwable, "%d%s", 1, "two");

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
  }

  @Test
  public void logMarkerThrowableFormatMessage() {
    LogLevel level = CRITICAL;
    Marker marker = NullMarker.INSTANCE;
    Throwable throwable = NullThrowable.INSTANCE;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, marker, throwable, "%d%S", 1, "two");

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
  }

  private final static String one = "one";
  private final static String two = "two";
  private final static String three = "three";
  private final static String four = "four";
  private final static String five = "five";
  private final static String six = "six";

  @Test
  public void logOneArg() {
    LogLevel level = INFO;
    Marker marker = null;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, "%s", one);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
  }

  @Test
  public void logTwoArgs() {
    LogLevel level = INFO;
    Marker marker = null;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, "%s%s", one, two);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
  }

  @Test
  public void logThreeArgs() {
    LogLevel level = INFO;
    Marker marker = null;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, "%s%s%s", one, two, three);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
  }

  @Test
  public void logFourArgs() {
    LogLevel level = INFO;
    Marker marker = null;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, "%s%s%s%s", one, two, three, four);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
  }

  @Test
  public void logMoreThan4Args() {
    LogLevel level = INFO;
    Marker marker = null;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, "%s%s%s%s%s%s", one, two, three, four, five, six);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
  }

  @Test
  public void logSupplier() {
    LogLevel level = WARN;
    Marker marker = null;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, ()->MSG);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(MSG)));
  }

  @Test
  public void logMarkerSupplier() {
    LogLevel level = WARN;
    Marker marker = NullMarker.INSTANCE;
    Throwable throwable = null;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, NullMarker.INSTANCE, ()->new StringBuilder(MSG));

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(MSG)));
    assertThat(record.getMarker(), is(NullMarker.INSTANCE));
    assertThat(record.getThrown(), is(nullValue()));
  }

  @Test
  public void logThrowableSupplier() {
    LogLevel level = ERROR;
    Marker marker = null;
    Throwable throwable = NullThrowable.INSTANCE;
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, throwable, ()->MSG);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo(MSG)));
    assertThat(record.getMarker(), is(nullValue()));
    assertThat(record.getThrown(), is(throwable));
  }

  @Test
  public void logMarkerThrowableSupplier() {
    LogLevel level = WARN;
    Marker marker = NullMarker.INSTANCE;
    Throwable throwable = new RuntimeException("Test");
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.log(level, marker, throwable, ()->new RuntimeException("Test"));

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should(times(1)).logImmediate(recordCaptor.capture());
    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
    assertThat(record.getMessage(), is(equalTo("java.lang.RuntimeException: Test")));
    assertThat(record.getMarker(), is(NullMarker.INSTANCE));
    assertThat(record.getThrown(), is(throwable));
  }

  @Test
  public void logCaught() {
    LogLevel level = ERROR;
    Marker marker = null;
    Throwable throwable = new RuntimeException("MeMeMe");
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    logger.caught(level, throwable);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
    assertThat(record.getThrown(), is(throwable));
  }

  @Test
  public void logThrowing() {
    LogLevel level = INFO;
    Marker marker = null;
    Throwable throwable = new RuntimeException("MeMeMe");
    when(realLogger.getLogEntry(level, marker, throwable, null))
        .thenReturn(ExtLogRecord.get(LOGGER_FQCN, level, "", marker, throwable, null, null));
    when(realLogger.isLoggable(level, marker, throwable)).thenReturn(true);
    JLoggerImpl logger = new JLoggerImpl(realLogger);
    Throwable returned = logger.throwing(level, throwable);

    assertThat(returned, is(throwable));
    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(realLogger).should().logImmediate(recordCaptor.capture());

    final ExtLogRecord record = recordCaptor.getValue();
    assertThat(record, is(notNullValue()));
    assertThat(record.getLevel(), is(level.getJdkLevel()));
    assertThat(record.getThrown(), is(throwable));
  }


}