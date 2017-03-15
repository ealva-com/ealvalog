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

package ealvalog.core;

import ealvalog.LogLevel;
import ealvalog.Marker;
import ealvalog.MarkerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test base logger
 * <p>
 * Created by Eric A. Snell on 3/2/17.
 */
@SuppressWarnings("PrivateMemberAccessBetweenOuterAndInnerClass")
public class BaseLoggerTest {

  private static final String SOME_MESSAGE = "some message";
  private static final String FORMAT = "%d";
  private LoggerImpl logger;
  private Marker markerParameter;
  private Marker loggerMarker;
  private LoggerImpl loggerWithMarker;
  private Throwable theThrowable;
  @SuppressWarnings("WeakerAccess") @Mock MarkerFactory markerFactory;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    logger = new LoggerImpl();
    markerParameter = new MarkerImpl("Marker", markerFactory);
    loggerMarker = new MarkerImpl("Marker", markerFactory);
    loggerWithMarker = new LoggerImpl(loggerMarker);
    theThrowable = new Throwable();
  }

  /**
   * Test BaseLogger doesn't log when subclass says not to
   */
  @Test
  public void testShouldNotLog() {
    logger.log(LogLevel.CRITICAL, "test");

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.level, is(nullValue()));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(0));
    assertThat(logger.msg, is(nullValue()));
    assertThat(logger.formatArgs, is(nullValue()));
  }

  /**
   * Test simple level and message log
   */
  @Test
  public void testSimple() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, SOME_MESSAGE);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(SOME_MESSAGE)));
    assertNoFormatArgs(logger);
  }

  private void assertNoFormatArgs(final LoggerImpl logger) {
    assertThat(logger.formatArgs, is(notNullValue()));
    assertThat(logger.formatArgs.length, is(0));
  }

  @Test
  public void testLoggerMarker() {
    loggerWithMarker.setShouldLog();
    loggerWithMarker.log(LogLevel.CRITICAL, SOME_MESSAGE);

    assertThat(loggerWithMarker.isLoggableInvoked, is(true));
    assertThat(loggerWithMarker.marker, is(loggerMarker));
    assertThat(loggerWithMarker.level, is(LogLevel.CRITICAL));
    assertThat(loggerWithMarker.throwable, is(nullValue()));
    assertThat(loggerWithMarker.stackDepth, is(2));
    assertThat(loggerWithMarker.msg, is(equalTo(SOME_MESSAGE)));
    assertNoFormatArgs(loggerWithMarker);
  }

  @Test
  public void testLoggerSetMarker() {
    loggerWithMarker.setShouldLog();
    loggerWithMarker.setMarker(markerParameter);
    loggerWithMarker.log(LogLevel.CRITICAL, SOME_MESSAGE);

    assertThat(loggerWithMarker.isLoggableInvoked, is(true));
    assertThat(loggerWithMarker.marker, is(markerParameter));
    assertThat(loggerWithMarker.level, is(LogLevel.CRITICAL));
    assertThat(loggerWithMarker.throwable, is(nullValue()));
    assertThat(loggerWithMarker.stackDepth, is(2));
    assertThat(loggerWithMarker.msg, is(equalTo(SOME_MESSAGE)));
    assertNoFormatArgs(loggerWithMarker);
  }

  @Test
  public void testLogWithMarker() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, markerParameter, SOME_MESSAGE);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(markerParameter));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(SOME_MESSAGE)));
    assertNoFormatArgs(logger);
  }

  @Test
  public void testWithThrowable() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, theThrowable, SOME_MESSAGE);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.throwable, is(theThrowable));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(SOME_MESSAGE)));
    assertNoFormatArgs(logger);

  }

  @Test
  public void testLogWithThrowableAndMarker() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, markerParameter, theThrowable, SOME_MESSAGE);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(markerParameter));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(theThrowable));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(SOME_MESSAGE)));
    assertNoFormatArgs(logger);
  }

  @Test
  public void testLogWith1FormatArgs() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, FORMAT, 1);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(1));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
  }

  @Test
  public void testLogWith2FormatArgs() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, FORMAT, 1, 2);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(2));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(logger.formatArgs[1].toString(), is(equalTo("2")));
  }

  @Test
  public void testLogWith3FormatArgs() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, FORMAT, 1, 2, 3);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(3));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(logger.formatArgs[1].toString(), is(equalTo("2")));
    assertThat(logger.formatArgs[2].toString(), is(equalTo("3")));
  }

  @Test
  public void testLogWith4FormatArgs() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, FORMAT, 1, 2, 3, 4);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(4));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(logger.formatArgs[1].toString(), is(equalTo("2")));
    assertThat(logger.formatArgs[2].toString(), is(equalTo("3")));
    assertThat(logger.formatArgs[3].toString(), is(equalTo("4")));
  }

  @Test
  public void testLogWith5FormatArgs() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, FORMAT, 1, 2, 3, 4, 5);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(5));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(logger.formatArgs[1].toString(), is(equalTo("2")));
    assertThat(logger.formatArgs[2].toString(), is(equalTo("3")));
    assertThat(logger.formatArgs[3].toString(), is(equalTo("4")));
    assertThat(logger.formatArgs[4].toString(), is(equalTo("5")));
  }

  @Test
  public void testLogWithMarkerAndArgs() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, markerParameter, FORMAT, 1, 2);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(markerParameter));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(nullValue()));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(2));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(logger.formatArgs[1].toString(), is(equalTo("2")));
  }

  @Test
  public void testWithThrowableAndArgs() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, theThrowable, FORMAT, 1, 2);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(nullValue()));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(theThrowable));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(2));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(logger.formatArgs[1].toString(), is(equalTo("2")));
  }

  @Test
  public void testLogWithThrowableAndMarkerAndArgs() {
    logger.setShouldLog();
    logger.log(LogLevel.CRITICAL, markerParameter, theThrowable, FORMAT, 1, 2);

    assertThat(logger.isLoggableInvoked, is(true));
    assertThat(logger.marker, is(markerParameter));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(theThrowable));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(2));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(logger.formatArgs[1].toString(), is(equalTo("2")));
  }

  @Test
  public void testLogImmediate() {
    logger.setShouldLog();
    logger.logImmediate(LogLevel.CRITICAL, markerParameter, theThrowable, 1, FORMAT, 1, 2);

    assertThat(logger.isLoggableInvoked, is(false));
    assertThat(logger.marker, is(markerParameter));
    assertThat(logger.level, is(LogLevel.CRITICAL));
    assertThat(logger.throwable, is(theThrowable));
    assertThat(logger.stackDepth, is(2));
    assertThat(logger.msg, is(equalTo(FORMAT)));
    assertThat(logger.formatArgs.length, is(2));
    assertThat(logger.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(logger.formatArgs[1].toString(), is(equalTo("2")));
  }

  @Test
  public void testLogImmediateNoMarkerParamameter() {
    loggerWithMarker.setShouldLog();
    loggerWithMarker.logImmediate(LogLevel.CRITICAL, theThrowable, 1, FORMAT, 1, 2);

    assertThat(loggerWithMarker.isLoggableInvoked, is(false));
    assertThat(loggerWithMarker.marker, is(loggerMarker));
    assertThat(loggerWithMarker.level, is(LogLevel.CRITICAL));
    assertThat(loggerWithMarker.throwable, is(theThrowable));
    assertThat(loggerWithMarker.stackDepth, is(2));
    assertThat(loggerWithMarker.msg, is(equalTo(FORMAT)));
    assertThat(loggerWithMarker.formatArgs.length, is(2));
    assertThat(loggerWithMarker.formatArgs[0].toString(), is(equalTo("1")));
    assertThat(loggerWithMarker.formatArgs[1].toString(), is(equalTo("2")));
  }


  class LoggerImpl extends ealvalog.core.BaseLogger {

    private static final String NAME = "";

    private String name = NAME;
    private boolean shouldLog = false;
    private LogLevel level = null;
    private Marker marker = null;
    private Throwable throwable = null;
    private int stackDepth = 0;
    private String msg = null;
    private Object[] formatArgs = null;
    private boolean includeLocation = false;

    LoggerImpl() {
      this(null);
    }

    LoggerImpl(final @Nullable Marker marker) {
      super(NAME, marker);

    }

    void setShouldLog() {
      this.shouldLog = true;
    }

    @Override public @NotNull String getName() {
      return name;
    }

    @Nullable @Override public LogLevel getLogLevel() {
      return null;
    }

    @Override public void setLogLevel(@Nullable final LogLevel logLevel) {

    }

    @NotNull @Override public LogLevel getEffectLogLevel() {
      return LogLevel.NONE;
    }

    @Override public void setIncludeLocation(final boolean includeLocation) {
      this.includeLocation = includeLocation;
    }

    @Override public boolean getIncludeLocation() {
      return includeLocation;
    }

    boolean isLoggableInvoked;

    @Override public boolean isLoggable(@NotNull final LogLevel level, final @Nullable Marker marker, final @Nullable Throwable throwable) {
      isLoggableInvoked = true;
      return shouldLog;
    }

    @Override
    protected void printLog(final @NotNull LogLevel level,
                            final @Nullable Marker marker,
                            final @Nullable Throwable throwable,
                            final int stackDepth,
                            final @NotNull String msg,
                            final @NotNull Object[] formatArgs) {

      this.level = level;
      this.marker = marker;
      this.throwable = throwable;
      this.stackDepth = stackDepth;
      this.msg = msg;
      this.formatArgs = formatArgs;
    }
  }
}
