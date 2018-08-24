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

import com.ealva.ealvalog.FilterResult;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.core.ExtLogRecord;
import com.ealva.ealvalog.util.NullThrowable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Eric A. Snell on 8/24/18.
 */
public class JdkFilterTest {
  private static String loggerName = "LoggerName";
  private static LogLevel level = LogLevel.INFO;
  private static Marker marker = NullMarker.INSTANCE;
  private static Throwable throwable = NullThrowable.INSTANCE;

  @Mock LoggerFilter loggerFilter;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void isLoggableNeutral() {
    when(loggerFilter.isLoggable(loggerName,
                                 level,
                                 marker,
                                 throwable)).thenReturn(FilterResult.NEUTRAL);
    JdkFilter filter = new JdkFilter(loggerFilter);
    assertThat(filter.isLoggable(loggerName, level, marker, throwable), is(FilterResult.NEUTRAL));
    then(loggerFilter).should(times(1)).isLoggable(loggerName, level, marker, throwable);
  }

  @Test
  public void isLoggableAccept() {
    when(loggerFilter.isLoggable(loggerName,
                                 level,
                                 marker,
                                 throwable)).thenReturn(FilterResult.ACCEPT);
    JdkFilter filter = new JdkFilter(loggerFilter);
    assertThat(filter.isLoggable(loggerName, level, marker, throwable), is(FilterResult.ACCEPT));
    then(loggerFilter).should(times(1)).isLoggable(loggerName, level, marker, throwable);
  }

  @Test
  public void isLoggableDeny() {
    when(loggerFilter.isLoggable(loggerName,
                                 level,
                                 marker,
                                 throwable)).thenReturn(FilterResult.DENY);
    JdkFilter filter = new JdkFilter(loggerFilter);
    assertThat(filter.isLoggable(loggerName, level, marker, throwable), is(FilterResult.DENY));
    then(loggerFilter).should(times(1)).isLoggable(loggerName, level, marker, throwable);
  }

  @Test
  public void isRecordLoggable() {
    when(loggerFilter.isLoggable(loggerName,
                                 level,
                                 marker,
                                 throwable)).thenReturn(FilterResult.DENY);
    JdkFilter filter = new JdkFilter(loggerFilter);
    try (ExtLogRecord record = ExtLogRecord.get(level, loggerName, marker, throwable)) {
      assertThat(filter.isLoggable(record), is(false));
      then(loggerFilter).should(times(1)).isLoggable(loggerName, level, marker, throwable);
    }
  }
}
