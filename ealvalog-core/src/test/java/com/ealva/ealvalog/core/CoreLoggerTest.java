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
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Marker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

/**
 * Test core functionality. Ensure it's calling the bridge correctly, including passing itself.
 * <p>
 * Created by Eric A. Snell on 3/16/17.
 */
public class CoreLoggerTest {
  private static final String MESSAGE = "Message";

  @SuppressWarnings("WeakerAccess") @Mock com.ealva.ealvalog.core.Bridge bridge;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testPrintLog() {
    com.ealva.ealvalog.core.CoreLogger<com.ealva.ealvalog.core.Bridge>
        logger = new CoreLoggerForTest("LoggerName", bridge);
    logger.printLog(LogLevel.CRITICAL, null, null, 1, MESSAGE);

    then(bridge).should(only()).log(same(logger),
                                    same(LogLevel.CRITICAL),
                                    isNull(Marker.class),
                                    isNull(Throwable.class),
                                    eq(2),
                                    eq(MESSAGE));
  }

  private static class CoreLoggerForTest extends CoreLogger<com.ealva.ealvalog.core.Bridge> {

    CoreLoggerForTest(@NotNull final String name, @NotNull final Bridge bridge) {
      super(name, bridge, null);
    }

    @Nullable @Override public LogLevel getLogLevel() {
      return null;
    }    @Override public void setLogToParent(final boolean logToParent) {

    }

    @Override public void setLogLevel(@Nullable final LogLevel logLevel) {

    }    @Override public boolean getLogToParent() {
      return true;
    }

    @NotNull @Override public LogLevel getEffectiveLogLevel() {
      return LogLevel.ALL;
    }    @Override public boolean shouldLogToParent() {
      return false;
    }

    @Override public boolean getIncludeLocation() {
      return false;
    }

    @Override public void setIncludeLocation(final boolean includeLocation) {

    }

    @Override
    public boolean isLoggable(@NotNull final LogLevel level,
                              @Nullable final Marker marker,
                              @Nullable final Throwable throwable) {
      return true;
    }







    @Override public void setFilter(@NotNull final LoggerFilter filter) {

    }
  }
}
