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

package com.ealva.ealvalog;

import com.ealva.ealvalog.util.NullThrowable;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;


/**
 * Simple tests for logger factory singleton
 * <p>
 * Created by Eric A. Snell on 3/5/17.
 */
@SuppressWarnings("WeakerAccess")
public class LoggersNoFactoryTest {

  /**
   * Ensure no NPE or uncaught exception when no logger factory is set
   */
  @Test
  public void testNoLoggerFactorySet() throws Exception {
    testLogger(com.ealva.ealvalog.Loggers.get());
    testLogger(com.ealva.ealvalog.Loggers.get("blah"));
    testLogger(com.ealva.ealvalog.Loggers.get(LoggersNoFactoryTest.class));
    testLogger(com.ealva.ealvalog.Loggers.getRoot());
    testLogger(com.ealva.ealvalog.Loggers.get(com.ealva.ealvalog.NullMarker.INSTANCE));
    testLogger(com.ealva.ealvalog.Loggers.get(LoggersNoFactoryTest.class, com.ealva.ealvalog.NullMarker.INSTANCE));
    testLogger(com.ealva.ealvalog.Loggers.get("blah", com.ealva.ealvalog.NullMarker.INSTANCE));
    com.ealva.ealvalog.Loggers.log(com.ealva.ealvalog.LogLevel.CRITICAL, "");
    com.ealva.ealvalog.Loggers.log(com.ealva.ealvalog.LogLevel.CRITICAL, com.ealva.ealvalog.NullMarker.INSTANCE, "");
    com.ealva.ealvalog.Loggers.log(com.ealva.ealvalog.LogLevel.CRITICAL, NullThrowable.INSTANCE, "");
    com.ealva.ealvalog.Loggers.log(com.ealva.ealvalog.LogLevel.CRITICAL, com.ealva.ealvalog.NullMarker.INSTANCE, NullThrowable.INSTANCE, "");
    Loggers.log(com.ealva.ealvalog.LogLevel.CRITICAL, "", 1, 2, 3);

  }

  private void testLogger(final Logger logger) {
    assertThat(logger, is(notNullValue()));
    assertThat(logger.getName(), is(notNullValue()));
    assertThat(logger.getLogLevel(), is(notNullValue()));
    assertThat(logger.getEffectLogLevel(), is(notNullValue()));
    assertThat(logger.getMarker(), is(notNullValue()));
    assertThat(logger.isLoggable(com.ealva.ealvalog.LogLevel.CRITICAL), is(false));
    assertThat(logger.isLoggable(LogLevel.CRITICAL, NullMarker.INSTANCE, NullThrowable.INSTANCE), is(false));

  }

}
