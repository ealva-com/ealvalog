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

package com.ealva.ealvalog.impl;

import com.ealva.ealvalog.ExtLogRecord;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Loggers;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.java.JLogger;
import com.ealva.ealvalog.java.JLoggers;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.when;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

@SuppressWarnings("deprecation") @RunWith(AndroidJUnit4.class)
public class AndroidLoggerTest {
  private static final String TAG = "AndroidLoggerTest";
  @SuppressWarnings("WeakerAccess") @Mock LogHandler mockHandler;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    AndroidLogger.Companion.setHandler(mockHandler);
    AndroidLoggerFactory.INSTANCE.clearLoggerCache();
    Loggers.INSTANCE.setFactory(AndroidLoggerFactory.INSTANCE);
  }

  @Test
  public void testPrintLog() {
    when(mockHandler.isLoggable(TAG, LogLevel.CRITICAL, null, null)).thenReturn(true);
    when(mockHandler.shouldIncludeLocation(TAG, Log.ASSERT, null, null)).thenReturn(false);

    //given
    final JLogger logger = JLoggers.get(AndroidLoggerTest.class);
    final String msg = "The msg";
    logger.log(LogLevel.CRITICAL, msg);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(mockHandler).should(atLeastOnce()).prepareLog(recordCaptor.capture());
    ExtLogRecord record = recordCaptor.getValue();
    assertThat(record.getLoggerName(), is(AndroidLoggerTest.class.getName()));
    assertThat(record.getLogLevel(), is(LogLevel.CRITICAL));
    assertThat(record.getMessage(), is(msg));
    assertThat(record.getThrown(), is(nullValue()));
    assertThat(record.getMarker(), is(CoreMatchers.<Marker>instanceOf(NullMarker.class)));
    assertThat(record.getCallLocation(), is(isNull()));
  }

  @Test
  public void testPrintLogHandlerWantsLocation() {
    when(mockHandler.shouldIncludeLocation(TAG, Log.WARN, null, null)).thenReturn(true);
    when(mockHandler.isLoggable(TAG, LogLevel.WARN, null, null)).thenReturn(true);

    //given
    final JLogger logger = JLoggers.get(AndroidLoggerTest.class);
    final String msg = "The msg";
    logger.setIncludeLocation(false);
    logger.log(LogLevel.WARN, msg);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(mockHandler).should(atLeastOnce()).prepareLog(recordCaptor.capture());
    ExtLogRecord record = recordCaptor.getValue();
    assertThat(record.getLoggerName(), is(AndroidLoggerTest.class.getName()));
    assertThat(record.getLogLevel(), is(LogLevel.WARN));
    assertThat(record.getMessage(), is(msg));
    assertThat(record.getThrown(), is(nullValue()));
    assertThat(record.getMarker(), is(CoreMatchers.<Marker>instanceOf(NullMarker.class)));
    assertThat(record.getCallLocation(), is(notNullValue()));
  }

  @Test
  public void testPrintLogSetIncludeLocation() {
    when(mockHandler.isLoggable(TAG, LogLevel.CRITICAL, null, null)).thenReturn(true);

    //given
    final JLogger logger = JLoggers.get(AndroidLoggerTest.class);
    logger.setIncludeLocation(true);
    final String msg = "The msg";
    logger.log(LogLevel.CRITICAL, msg);

    ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
    then(mockHandler).should(atLeastOnce()).prepareLog(recordCaptor.capture());
    ExtLogRecord record = recordCaptor.getValue();
    assertThat(record.getLoggerName(), is(AndroidLoggerTest.class.getName()));
    assertThat(record.getLogLevel(), is(LogLevel.CRITICAL));
    assertThat(record.getMessage(), is(msg));
    assertThat(record.getThrown(), is(nullValue()));
    assertThat(record.getMarker(), is(CoreMatchers.<Marker>instanceOf(NullMarker.class)));
    assertThat(record.getCallLocation(), is(notNullValue()));
  }

}
