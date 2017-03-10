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
import ealvalog.Marker;
import ealvalog.TheLoggerFactory;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import android.support.test.runner.AndroidJUnit4;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AndroidLoggerTest {
  private MockLogHandler handler;

  @Before
  public void setup() {
    handler = new MockLogHandler();
    AndroidLogger.setHandler(handler);
    TheLoggerFactory.setFactory(new AndroidLoggerFactory());
  }

  @Test
  public void testDebugHandler() {
    final Logger logger = TheLoggerFactory.get();
    logger.log(LogLevel.CRITICAL, "The msg");
    assertThat(handler.prepareLogInvoked, is(true));
    assertThat(handler.prepareLogCallerLocation.getClassName(), is(equalTo(AndroidLoggerTest.class.getName())));
  }

  @SuppressWarnings("WeakerAccess")
  public static final class MockLogHandler implements LogHandler {

    public boolean isLoggableInvoked = false;
    public String isLoggableTag = null;
    public int isLoggableLevel = 0;
    public boolean isLoggableReturn = true;
    @Override public boolean isLoggable(@NotNull final String tag, final int level) {
      isLoggableInvoked = true;
      isLoggableTag = tag;
      isLoggableLevel = level;
      return isLoggableReturn;
    }

    public boolean shouldIncludeLocationInvoked = false;
    public boolean shouldIncludeLocatoinReturn = true;
    @Override
    public boolean shouldIncludeLocation(@NotNull final String tag,
                                         final int level,
                                         @Nullable final Marker marker,
                                         @Nullable final Throwable throwable) {
      shouldIncludeLocationInvoked = true;
      return shouldIncludeLocatoinReturn;
    }

    public boolean prepareLogInvoked = false;
    public StackTraceElement prepareLogCallerLocation = null;
    @Override
    public void prepareLog(final String tag,
                           final int level,
                           @Nullable final Marker marker,
                           @Nullable final Throwable throwable,
                           @Nullable final StackTraceElement callerLocation,
                           @NotNull final LogMessageFormatter formatter,
                           @NotNull final String msg,
                           @NotNull final Object... formatArgs) {
      prepareLogInvoked = true;
      prepareLogCallerLocation = callerLocation;
    }
  }
}
