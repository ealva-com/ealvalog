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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;


/**
 * Simple tests for logger factory singleton
 * <p>
 * Created by Eric A. Snell on 3/5/17.
 */
@SuppressWarnings("WeakerAccess")
public class LoggersTest {
  @Mock LoggerFactory mockFactory;
  @Mock com.ealva.ealvalog.Logger mockLogger;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    Loggers.INSTANCE.setFactory(mockFactory);
  }

  @After
  public void tearDown() {
    Loggers.INSTANCE.setFactory(NullLoggerFactory.INSTANCE);
  }

  @Test
  public void testUnspecifiedLoggerName() {
    given(mockFactory.get(LoggersTest.class.getName(), null, false)).willReturn(mockLogger);

    // when
    final Logger logger = Loggers.INSTANCE.get(LoggersTest.class.getName(), null, false);

    then(mockFactory).should(only()).get(LoggersTest.class.getName(), null, false);
    assertThat(logger, is(mockLogger));  // not really testing anything, but using that variable
  }
}
