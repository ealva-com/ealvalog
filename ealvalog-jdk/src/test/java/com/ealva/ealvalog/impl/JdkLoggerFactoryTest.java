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

package com.ealva.ealvalog.impl;

import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.LoggerFactory;
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.Loggers;
import com.ealva.ealvalog.filter.AlwaysAcceptFilter;

import static com.ealva.ealvalog.LoggerFactory.ROOT_LOGGER_NAME;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test {@link LoggerFactory} implementation that use the java.util.logging.Logger as the underlying logger.
 * Created by Eric A. Snell on 3/7/17.
 */
public class JdkLoggerFactoryTest {
  private JdkLogger theRootLogger;
  private JdkBridge theRootBridge;

  @Before
  public void setup() {
    final JdkLoggerFactory loggerFactory = JdkLoggerFactory.INSTANCE;
    theRootLogger = JdkLoggerFactory.INSTANCE.getForTest(ROOT_LOGGER_NAME);
    theRootBridge = theRootLogger.getBridgeForTest();
    Loggers.INSTANCE.setFactory(loggerFactory);
  }

  @Test
  public void testGetRootLogger() {
    final Logger rootByName = Loggers.INSTANCE.get("", null, false);
    assertThat(rootByName, is(notNullValue()));
    assertThat(theRootLogger, is(rootByName));
    assertThat(theRootLogger.getBridgeForTest(), is(theRootBridge));

    final Logger root = Loggers.INSTANCE.getRoot();
    assertThat(root, is(notNullValue()));

    assertThat(root, is(rootByName));
    assertThat(rootByName.getName(), is(equalTo(ROOT_LOGGER_NAME)));

    JdkLogger directRoot = JdkLoggerFactory.INSTANCE.getForTest("");
    assertThat(directRoot, is(notNullValue()));
    assertThat(directRoot, is(root));

    JdkLogger anotherDirect = JdkLoggerFactory.INSTANCE.getForTest("");
    assertThat(anotherDirect, is(notNullValue()));
    assertThat(anotherDirect, is(directRoot));

    final JdkBridge directBridge = directRoot.getBridgeForTest();
    assertThat(directBridge, is(notNullValue()));

    final JdkBridge anotherBridge = anotherDirect.getBridgeForTest();
    assertThat(anotherBridge, is(notNullValue()));
    assertThat(directBridge, is(anotherBridge));

    assertThat(directBridge.getName(), is(equalTo(ROOT_LOGGER_NAME)));
    assertThat(directBridge.getParent(), is(nullValue()));
  }

  @Test
  public void testBridgeIsRoot() {
    final Logger logger = Loggers.INSTANCE.get(this.getClass().getName(), null, false);

    final JdkLogger jdkLogger = JdkLoggerFactory.INSTANCE.getForTest(this.getClass().getName());
    assertThat(jdkLogger, is(logger));

    final JdkBridge bridge = jdkLogger.getBridgeForTest();
    assertThat(bridge, is(theRootBridge));  // no filter set so will be the root
  }

  @Test
  public void testSetFilter() {
    final Logger logger = Loggers.INSTANCE.get(this.getClass().getName(), null, false);

    final JdkLogger jdkLogger = JdkLoggerFactory.INSTANCE.getForTest(this.getClass().getName());
    assertThat(jdkLogger, is(logger));

    JdkBridge bridge = jdkLogger.getBridgeForTest();
    assertThat(bridge, is(theRootBridge));  // no filter set so will be the root

    LoggerFilter dummy = AlwaysAcceptFilter.INSTANCE;
    jdkLogger.setFilter(dummy);

    bridge = jdkLogger.getBridgeForTest();
    assertThat(bridge, is(not(theRootBridge)));  // should have a new bridge
    assertThat(bridge.getFilter(), is(dummy));

    assertThat(bridge.getParent(), is(theRootBridge));

  }

  @Test
  public void testReset() {
    JdkLogger jdkLogger = JdkLoggerFactory.INSTANCE.getForTest(this.getClass().getName());

    JdkBridge bridge = jdkLogger.getBridgeForTest();
    assertThat(bridge, is(theRootBridge));  // no filter set so will be the root

    LoggerFilter dummy = AlwaysAcceptFilter.INSTANCE;
    jdkLogger.setFilter(dummy);
    bridge = jdkLogger.getBridgeForTest();
    assertThat(bridge, is(not(theRootBridge)));  // should have a new bridge
    assertThat(bridge.getFilter(), is(dummy));
    assertThat(bridge.getParent(), is(theRootBridge));

    JdkLoggerFactory.INSTANCE.reset();
    jdkLogger = JdkLoggerFactory.INSTANCE.getForTest(this.getClass().getName());
    bridge = jdkLogger.getBridgeForTest();
    assertThat(bridge, is(theRootBridge));  // no filter set so will be the root
  }

}
