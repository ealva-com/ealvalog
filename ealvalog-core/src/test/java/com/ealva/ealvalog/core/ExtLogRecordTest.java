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

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullMarker;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by Eric A. Snell on 7/4/18.
 */
public class ExtLogRecordTest {

  @Before
  public void setup() {
    // ensure that tests related to caching of the record don't interfere with each other
    ExtLogRecord.clearCachedRecord();
  }

  @Test
  public void testGet() {
    final LogLevel level = LogLevel.ERROR;
    final String loggerName = "LoggerName";
    final Marker marker = Markers.INSTANCE.get("marker");
    //noinspection ThrowableNotThrown
    final Throwable throwable = new RuntimeException();
    final ExtLogRecord first = ExtLogRecord.get(level, loggerName, marker, throwable);
    assertThat(first.getLogLevel(), is(level));
    assertThat(first.getLoggerName(), is(loggerName));
    assertThat(first.getMarker(), is(marker));
    assertThat(first.getThrown(), is(throwable));
    first.close();
    final LogLevel secondLevel = LogLevel.WARN;
    final String secondLoggerName = "Other";
    final Marker secondMarker = NullMarker.INSTANCE;
    final Throwable secondThrowable = null;
    final ExtLogRecord
        second =
        ExtLogRecord.get(secondLevel, secondLoggerName, secondMarker, secondThrowable);
    assertThat(second, sameInstance(first));
    assertThat(second.getLogLevel(), is(secondLevel));
    assertThat(second.getLoggerName(), is(secondLoggerName));
    assertThat(second.getMarker(), is(secondMarker));
    assertThat(second.getThrown(), is(secondThrowable));
  }

  @Test
  public void testGetCachedRecord() {
    final LogLevel level = LogLevel.ERROR;
    final String loggerName = "LoggerName";
    final ExtLogRecord first = ExtLogRecord.get(level, loggerName, null, null);
    final String firstParm = "first";
    final String secondParm = "second";
    first.setParameters(new Object[]{firstParm, secondParm});
    assertThat(first.getLogLevel(), is(level));
    assertThat(first.getLoggerName(), is(loggerName));
    assertThat(first.getParameterCount(), is(2));
    Object[] firstParameters = first.getParameters();
    assertThat((String)firstParameters[0], is(firstParm));
    assertThat((String)firstParameters[1], is(secondParm));
    first.close();

    final LogLevel secondLevel = LogLevel.WARN;
    final String secondLoggerName = "Other";
    final String secondMessage = "%s %d %f";
    final ExtLogRecord
        second =
        ExtLogRecord.get(secondLevel, secondLoggerName, null, null);
    second.setMessage(secondMessage);
    assertThat(second.getLogLevel(), is(secondLevel));
    assertThat(second.getLoggerName(), is(secondLoggerName));
    Object[] secondParameters = first.getParameters();
    assertThat(secondParameters, notNullValue());
    assertThat(secondParameters.length, greaterThanOrEqualTo(firstParameters.length));
    assertThat(secondParameters[0], nullValue());
    assertThat(secondParameters[1], nullValue());
  }

  @Test
  public void testGetMultiple() {
    final ExtLogRecord first = ExtLogRecord.get(LogLevel.ERROR, "LoggerName", null, null);
    final ExtLogRecord
        second =
        ExtLogRecord.get(LogLevel.ERROR, "LoggerName", null, null);
    assertThat(first, not(sameInstance(second)));
    first.close();
    second.close();
    final LogLevel logLevel = LogLevel.WARN;
    final String loggerName = "Other";
    final ExtLogRecord third = ExtLogRecord.get(logLevel, loggerName, null, null);
    assertThat(first, sameInstance(third));
    assertThat(third.getLogLevel(), is(logLevel));
    assertThat(third.getLoggerName(), is(loggerName));
  }


}
