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

import ealvalog.FilterResult;
import ealvalog.Logger;
import ealvalog.NullMarker;
import ealvalog.filter.AlwaysAcceptFilter;
import ealvalog.util.NullThrowable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static ealvalog.LogLevel.CRITICAL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class AndroidLoggerHandlerTest {
  @SuppressWarnings("WeakerAccess") @Mock Logger logger;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testIsLoggableNoFilter() throws Exception {
    when(logger.getName()).thenReturn(AndroidLoggerHandlerTest.class.getName());

    AndroidLoggerHandler handler = AndroidLoggerHandler.builder().build();
    assertThat(handler.isLoggable(logger, CRITICAL, NullMarker.INSTANCE, NullThrowable.INSTANCE),
               is(FilterResult.NEUTRAL));
  }

  @Test
  public void testIsLoggableAlwaysAcceptFilter() throws Exception {
    when(logger.getName()).thenReturn(AndroidLoggerHandlerTest.class.getName());

    AndroidLoggerHandler handler = AndroidLoggerHandler.builder()
                                                       .filter(AlwaysAcceptFilter.INSTANCE)
                                                       .build();
    assertThat(handler.isLoggable(logger, CRITICAL, NullMarker.INSTANCE, NullThrowable.INSTANCE),
               is(FilterResult.ACCEPT));
  }
}
