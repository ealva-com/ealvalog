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

package com.ealva.ealvalog.log4j;

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.Markers;
import com.ealva.ealvalog.java.JLogger;
import com.ealva.ealvalog.java.JLoggers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Eric A. Snell on 9/6/18.
 */
public class Log4jLoggerAdapterTest {
  @Mock Appender appender;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(appender.isStarted()).thenReturn(true);
    when(appender.getName()).thenReturn("Appender");
    final Logger rootLogger = (Logger)LogManager.getRootLogger();
    rootLogger.addAppender(appender);
    new Log4jConfiguration().configure();
  }

  @After
  public void tearDown() {
    ((org.apache.logging.log4j.core.Logger)LogManager.getLogger()).removeAppender(appender);
  }

  @Test
  public void testLog() {
    ArgumentCaptor<LogEvent> logEventCaptor = ArgumentCaptor.forClass(LogEvent.class);
    JLogger logger = JLoggers.get("Test");
    final String message = "Message";
    logger.log(LogLevel.ERROR, message);
    then(appender).should(times(1)).append(logEventCaptor.capture());
    LogEvent event = logEventCaptor.getValue();
    assertThat(event.getLoggerFqcn(), is(Log4jLoggerAdapter.class.getName()));
    assertThat(event.getLevel(), is(Level.ERROR));
    assertThat(event.getMessage().getFormattedMessage(), is(message));
  }

  @Test
  public void testLogParams() {
    ArgumentCaptor<LogEvent> logEventCaptor = ArgumentCaptor.forClass(LogEvent.class);
    JLogger logger = JLoggers.get("Test");
    final String message = "Message";
    final String format = message + " {}";
    final int arg = 1;
    final String expected = message + " " + arg;
    logger.log(LogLevel.ERROR, format, arg);
    then(appender).should(times(1)).append(logEventCaptor.capture());
    LogEvent event = logEventCaptor.getValue();
    assertThat(event.getLoggerFqcn(), is(Log4jLoggerAdapter.class.getName()));
    assertThat(event.getLevel(), is(Level.ERROR));
    assertThat(event.getMessage().getFormattedMessage(), is(expected));
  }

  @Test
  public void testLogThrown() {
    ArgumentCaptor<LogEvent> logEventCaptor = ArgumentCaptor.forClass(LogEvent.class);
    JLogger logger = JLoggers.get("Test");
    final String message = "Message";
    final String format = message + " {}";
    final int arg = 1;
    final String expected = message + " " + arg;
    final Exception ex = new RuntimeException("");
    logger.log(LogLevel.ERROR, ex, format, arg);
    then(appender).should(times(1)).append(logEventCaptor.capture());
    LogEvent event = logEventCaptor.getValue();
    assertThat(event.getLoggerFqcn(), is(Log4jLoggerAdapter.class.getName()));
    assertThat(event.getLevel(), is(Level.ERROR));
    assertThat(event.getMessage().getFormattedMessage(), is(expected));
    assertThat(event.getThrown(), is(ex));
    assertThat(event.getThrownProxy().getThrowable(), is(ex));
  }

  @Test
  public void testLogMarkerAndThrown() {
    ArgumentCaptor<LogEvent> logEventCaptor = ArgumentCaptor.forClass(LogEvent.class);
    JLogger logger = JLoggers.get("Test");
    final String message = "Message";
    final String format = message + " {}";
    final int arg = 1;
    final String expected = message + " " + arg;
    final Exception ex = new RuntimeException("");
    final String markerName = "MarkerName";
    final Marker marker = Markers.INSTANCE.get(markerName);
    logger.log(LogLevel.ERROR, marker, ex, format, arg);
    then(appender).should(times(1)).append(logEventCaptor.capture());
    LogEvent event = logEventCaptor.getValue();
    assertThat(event.getLoggerFqcn(), is(Log4jLoggerAdapter.class.getName()));
    assertThat(event.getLevel(), is(Level.ERROR));
    assertThat(event.getMessage().getFormattedMessage(), is(expected));
    assertThat(event.getThrown(), is(ex));
    assertThat(event.getThrownProxy().getThrowable(), is(ex));
    assertThat(event.getMarker(), is(instanceOf(org.apache.logging.log4j.Marker.class)));
    assertThat(event.getMarker().getName(), is(markerName));
  }

  @Test
  public void testLogOtherInfo() {
    final long mills = System.currentTimeMillis();
    final long nanos = System.nanoTime();
    ArgumentCaptor<LogEvent> logEventCaptor = ArgumentCaptor.forClass(LogEvent.class);
    JLogger logger = JLoggers.get("Test");
    final String message = "Message";
    logger.log(LogLevel.ERROR, message);
    then(appender).should(times(1)).append(logEventCaptor.capture());
    LogEvent event = logEventCaptor.getValue();
    assertThat(event.getLoggerFqcn(), is(Log4jLoggerAdapter.class.getName()));
    assertThat(event.getLevel(), is(Level.ERROR));
    assertThat(event.getMessage().getFormattedMessage(), is(message));
    final Thread currentThread = Thread.currentThread();
    assertThat(event.getThreadId(), is(currentThread.getId()));
    assertThat(event.getThreadPriority(), is(currentThread.getPriority()));
    assertThat(event.getTimeMillis(),
               is(both(greaterThanOrEqualTo(mills)).and(lessThanOrEqualTo(System.currentTimeMillis()))));
    assertThat(event.getNanoTime(),
               is(both(greaterThanOrEqualTo(nanos)).and(lessThanOrEqualTo(System.nanoTime()))));
  }

}
