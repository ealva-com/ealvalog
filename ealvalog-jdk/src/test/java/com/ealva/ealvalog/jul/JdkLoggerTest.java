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

package com.ealva.ealvalog.jul;

import com.ealva.ealvalog.core.ExtLogRecord;
import com.ealva.ealvalog.LogLevel;

import static com.ealva.ealvalog.LogLevel.CRITICAL;
import static com.ealva.ealvalog.LogLevel.ERROR;
import static com.ealva.ealvalog.LogLevel.TRACE;
import static com.ealva.ealvalog.LogLevel.WARN;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Test the JdkLogger ensuring it's properly configuring both the bridge and the underlying java.util.logging.Logger
 * <p>
 * Created by Eric A. Snell on 3/16/17.
 */
public class JdkLoggerTest {
  //  private static final String PARENT_NAME = "parent";
  private static final String LOGGER_FQCN = "com.acme.loggers.TheLogger";
  private static final String CHILD_NAME = "parent.child";
  private static final String MSG = "=The Message=";

  @SuppressWarnings("WeakerAccess") @Mock Handler rootHandler;
  //  @SuppressWarnings("WeakerAccess") @Mock BaseLoggerHandler parentHandler;
  @SuppressWarnings("WeakerAccess") @Mock Handler childHandler;
//  @SuppressWarnings("WeakerAccess") @Mock Marker marker;
//  @SuppressWarnings("WeakerAccess") @Mock Throwable throwable;

  private JdkLogger rootLogger;
  //  private JdkLogger parentLogger;
  private JdkLogger childLogger;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    final JdkLoggerFactory loggerFactory = JdkLoggerFactory.INSTANCE;
    loggerFactory.reset(true);

    rootLogger = loggerFactory.getRoot();
//    parentLogger = loggerFactory.get(PARENT_NAME);
    childLogger = loggerFactory.get(CHILD_NAME);

    rootLogger.addHandler(rootHandler);
  }

  @Test
  public void testChildLogsToRootHandler() {
    try (ExtLogRecord extRecord = ExtLogRecord.get(LOGGER_FQCN, CRITICAL, CHILD_NAME, null, null)) {
      extRecord.append(MSG);
      childLogger.logImmediate(extRecord);

      ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
      then(rootHandler).should(only()).publish(recordCaptor.capture());

      final ExtLogRecord record = recordCaptor.getValue();
      assertThat(record, is(notNullValue()));
      assertThat(record.getLevel(), is(CRITICAL.getJdkLevel()));
      assertThat(record.getMessage(), is(equalTo(MSG)));
    }
  }


  @Test
  public void testSetLogLevel() {
    rootLogger.setLogLevel(TRACE);
    try (ExtLogRecord extRecord = ExtLogRecord.get(LOGGER_FQCN, TRACE, CHILD_NAME, null, null)) {
      extRecord.append(MSG);
      childLogger.logImmediate(extRecord);

      ArgumentCaptor<ExtLogRecord> recordCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
      then(rootHandler).should(only()).publish(recordCaptor.capture());

      final ExtLogRecord record = recordCaptor.getValue();
      assertThat(record, is(notNullValue()));
      assertThat(record.getLevel(), is(TRACE.getJdkLevel()));
      assertThat(record.getMessage(), is(equalTo(MSG)));
    }
  }

  @Test
  public void testChildLogsToHandlerAndRootHandler() {
    childLogger.addHandler(childHandler);
    assertThat(childLogger.willLogToParent(), is(true));

    try (ExtLogRecord extRecord = ExtLogRecord.get(LOGGER_FQCN, WARN, CHILD_NAME, null, null)) {
      extRecord.append(MSG);
      childLogger.logImmediate(extRecord);

      ArgumentCaptor<ExtLogRecord> rootCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
      then(rootHandler).should(only()).publish(rootCaptor.capture());

      final ExtLogRecord rootRecord = rootCaptor.getValue();
      assertThat(rootRecord, is(notNullValue()));
      assertThat(rootRecord.getLevel(), is(LogLevel.WARN.getJdkLevel()));
      assertThat(rootRecord.getMessage(), is(equalTo(MSG)));

      ArgumentCaptor<ExtLogRecord> childCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
      then(childHandler).should(only()).publish(childCaptor.capture());

      final ExtLogRecord childRecord = childCaptor.getValue();
      assertThat(childRecord, is(notNullValue()));
      assertThat(childRecord.getLevel(), is(LogLevel.WARN.getJdkLevel()));
      assertThat(childRecord.getMessage(), is(equalTo(MSG)));
    }
  }

  @Test
  public void testParentingChangeDueToLevel() {
    rootLogger.setLogLevel(ERROR);
    final Logger jdkLogger = LogManager.getLogManager().getLogger("");
    assertThat(jdkLogger.getLevel(), is(ERROR.getJdkLevel()));
    childLogger.setLogLevel(TRACE);
    final Logger jdkChildLogger = LogManager.getLogManager().getLogger(CHILD_NAME);
    assertThat(jdkChildLogger, is(notNullValue()));
    assertThat(jdkChildLogger.getLevel(), is(TRACE.getJdkLevel()));
    assertThat(jdkLogger.getLevel(), is(ERROR.getJdkLevel()));
  }

  @Test
  public void testChildLowerLevel() {
    rootLogger.setLogLevel(ERROR);

    childLogger.addHandler(childHandler);
    childLogger.setLogLevel(TRACE);

    try (ExtLogRecord extRecord = ExtLogRecord.get(LOGGER_FQCN, TRACE, CHILD_NAME, null, null)) {
      extRecord.append(MSG);
      childLogger.logImmediate(extRecord);

      ArgumentCaptor<ExtLogRecord> childCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
      then(childHandler).should(only()).publish(childCaptor.capture());

      final ExtLogRecord childRecord = childCaptor.getValue();
      assertThat(childRecord, is(notNullValue()));
      assertThat(childRecord.getLevel(), is(TRACE.getJdkLevel()));
      assertThat(childRecord.getMessage(), is(equalTo(MSG)));

      ArgumentCaptor<ExtLogRecord> rootCaptor = ArgumentCaptor.forClass(ExtLogRecord.class);
      then(rootHandler).should(only()).publish(rootCaptor.capture());

      final ExtLogRecord rootRecord = rootCaptor.getValue();
      assertThat(rootRecord, is(notNullValue()));
      assertThat(rootRecord.getLevel(), is(TRACE.getJdkLevel()));
      assertThat(rootRecord.getMessage(), is(equalTo(MSG)));
    }
  }

  @Test
  public void testChildHigherLevel() {
    rootLogger.setLogLevel(TRACE);

    childLogger.addHandler(childHandler);
    childLogger.setLogLevel(ERROR);

    logAndVerifyNoPublish(TRACE);
  }

  @Test
  public void testDoNotLogToParent() {
    childLogger.setLogToParent(false);
    logAndVerifyNoPublish(CRITICAL);
  }

  private void logAndVerifyNoPublish(final LogLevel logLevel) {
    try (ExtLogRecord extRecord = ExtLogRecord.get(LOGGER_FQCN, logLevel, CHILD_NAME, null, null)) {
      extRecord.append(MSG);
      childLogger.logImmediate(extRecord);

      verify(rootHandler, never()).publish(any(ExtLogRecord.class));
      verify(childHandler, never()).publish(any(ExtLogRecord.class));
    }
  }

}
