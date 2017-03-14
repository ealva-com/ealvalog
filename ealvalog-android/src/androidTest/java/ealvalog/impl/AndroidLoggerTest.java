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
import ealvalog.Logger;
import ealvalog.Loggers;
import ealvalog.Marker;
import ealvalog.util.LogMessageFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
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
    AndroidLogger.setHandler(mockHandler);
    Loggers.setFactory(new AndroidLoggerFactory());
  }

  @Test
  public void testPrintLog() {
    when(mockHandler.isLoggable(TAG, Log.ASSERT)).thenReturn(true);

    //given
    final Logger logger = Loggers.get();
    final String msg = "The msg";
    logger.log(LogLevel.CRITICAL, msg);

    then(mockHandler).should(atLeastOnce()).prepareLog(eq("AndroidLoggerTest"),
                                                       same(Log.ASSERT),
                                                       isNull(Marker.class),
                                                       isNull(Throwable.class),
                                                       isNull(StackTraceElement.class),
                                                       any(LogMessageFormatter.class),
                                                       eq(msg));
  }

  @Test
  public void testPrintLogHandlerWantsLocation() {
    when(mockHandler.shouldIncludeLocation(TAG, Log.ASSERT, null, null)).thenReturn(true);
    when(mockHandler.isLoggable(TAG, Log.ASSERT)).thenReturn(true);

    //given
    final Logger logger = Loggers.get();
    final String msg = "The msg";
    logger.log(LogLevel.CRITICAL, msg);

    then(mockHandler).should(atLeastOnce()).prepareLog(eq("AndroidLoggerTest"),
                                                       same(Log.ASSERT),
                                                       isNull(Marker.class),
                                                       isNull(Throwable.class),
                                                       any(StackTraceElement.class),
                                                       any(LogMessageFormatter.class),
                                                       eq(msg));
  }

  @Test
  public void testPrintLogSetIncludeLocation() {
    when(mockHandler.isLoggable(TAG, Log.ASSERT)).thenReturn(true);

    //given
    final Logger logger = Loggers.get();
    logger.setIncludeLocation(true);
    final String msg = "The msg";
    logger.log(LogLevel.CRITICAL, msg);

    then(mockHandler).should(atLeastOnce()).prepareLog(eq("AndroidLoggerTest"),
                                                       same(Log.ASSERT),
                                                       isNull(Marker.class),
                                                       isNull(Throwable.class),
                                                       any(StackTraceElement.class),
                                                       any(LogMessageFormatter.class),
                                                       eq(msg));
  }

}
