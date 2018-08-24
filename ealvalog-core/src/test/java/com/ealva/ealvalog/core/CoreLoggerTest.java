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

import com.ealva.ealvalog.LogEntry;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullLogEntry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;

/**
 * Test core functionality. Ensure it's calling the bridge correctly, including passing itself.
 * <p>
 * Created by Eric A. Snell on 3/16/17.
 */
public class CoreLoggerTest {
  private static final String MESSAGE = "Message";
  private static final String LOGGER_NAME = "LoggerName";

  @Mock com.ealva.ealvalog.core.Bridge bridge;
  @Mock LoggerConfiguration<Bridge> configuration;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(configuration.getBridge(LOGGER_NAME)).thenReturn(bridge);
  }

  @SuppressWarnings("deprecation") // isNull()
  @Test
  public void testPrintLog() {
    com.ealva.ealvalog.core.CoreLogger<com.ealva.ealvalog.core.Bridge>
        logger = new CoreLoggerForTest(LOGGER_NAME, configuration);
    final ExtLogRecord record = ExtLogRecord.get(LogLevel.WARN, "", null, null);
    record.append(MESSAGE);
    logger.logImmediate(record);
    then(bridge).should(only()).log(same(record));
  }

  private static class CoreLoggerForTest extends CoreLogger<com.ealva.ealvalog.core.Bridge> {
    private final @NotNull String name;

    CoreLoggerForTest(@NotNull final String name,
                      @NotNull final LoggerConfiguration<Bridge> configuration) {
      super(name, configuration);
      this.name = name;
    }

    @Nullable @Override public LogLevel getLogLevel() {
      return null;
    }

    @Override public void setLogLevel(@Nullable final LogLevel logLevel) {

    }

    @NotNull @Override public LogLevel getEffectiveLogLevel() {
      return LogLevel.ALL;
    }

    @Override public boolean getIncludeLocation() {
      return false;
    }

    @Override
    public boolean shouldIncludeLocation(@NotNull final LogLevel logLevel,
                                         @Nullable final Marker marker,
                                         @Nullable final Throwable throwable) {
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

    @NotNull @Override public String getName() {
      return name;
    }

    @Nullable @Override public Marker getMarker() {
      return null;
    }

    @Override public void setMarker(@Nullable final Marker marker) {

    }

    @NotNull @Override
    public LogEntry getLogEntry(@NotNull final LogLevel logLevel,
                                @Nullable final Marker marker,
                                @Nullable final Throwable throwable) {
      return NullLogEntry.INSTANCE;
    }

    @Override public void logImmediate(@NotNull final LogEntry entry) {
      getBridge().log(entry);
    }
  }
}
