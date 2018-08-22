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

package com.ealva.ealvalog.java;

import com.ealva.ealvalog.Loggers;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.NullLoggerFactory;
import com.ealva.ealvalog.NullMarker;
import com.ealva.ealvalog.jul.JdkLoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Eric A. Snell on 8/13/18.
 */
public class JLoggersTest {
  private static final String NAME = "Name";
  private static final Marker MARKER = NullMarker.INSTANCE;

  @Before
  public void setup() {
    JdkLoggerFactory.INSTANCE.reset();
    Loggers.INSTANCE.setFactory(JdkLoggerFactory.INSTANCE);
  }

  @After
  public void teardown() {
    Loggers.INSTANCE.setFactory(NullLoggerFactory.INSTANCE);
  }

  @Test
  public void getGetWithName() {
    JLogger logger = JLoggers.get(NAME);
    assertThat(logger.getName(), is(NAME));
    assertThat(logger.getMarker(), is(nullValue()));
    assertThat(logger.getIncludeLocation(), is(false));
  }

  @Test
  public void getRoot() {
    JLogger logger = JLoggers.getRoot();
    assertThat(logger.getName(), is(""));
    assertThat(logger.getMarker(), is(nullValue()));
    assertThat(logger.getIncludeLocation(), is(false));
  }

  @Test
  public void getWithMarker() {
    JLogger logger = JLoggers.get(NAME, MARKER);
    assertThat(logger.getName(), is(NAME));
    assertThat(logger.getMarker(), is(MARKER));
    assertThat(logger.getIncludeLocation(), is(false));
  }

  @Test
  public void getIncludeLocation() {
    JLogger logger = JLoggers.get(NAME, MARKER, true);
    assertThat(logger.getName(), is(NAME));
    assertThat(logger.getMarker(), is(MARKER));
    assertThat(logger.getIncludeLocation(), is(true));
  }

  @Test
  public void getWithClass() {
    JLogger logger = JLoggers.get(JLoggersTest.class);
    assertThat(logger.getName(), is(JLoggersTest.class.getName()));
    assertThat(logger.getMarker(), is(nullValue()));
    assertThat(logger.getIncludeLocation(), is(false));
  }

  @Test
  public void getWithClassMarker() {
    JLogger logger = JLoggers.get(JLoggersTest.class, MARKER);
    assertThat(logger.getName(), is(JLoggersTest.class.getName()));
    assertThat(logger.getMarker(), is(MARKER));
    assertThat(logger.getIncludeLocation(), is(false));
  }

  @Test
  public void getWithClassMarkerLocation() {
    JLogger logger = JLoggers.get(JLoggersTest.class, MARKER, true);
    assertThat(logger.getName(), is(JLoggersTest.class.getName()));
    assertThat(logger.getMarker(), is(MARKER));
    assertThat(logger.getIncludeLocation(), is(true));
  }

  @Test
  public void get() {
    JLogger logger = JLoggers.get();
    assertThat(logger.getName(), is(JLoggersTest.class.getName()));
    assertThat(logger.getMarker(), is(nullValue()));
    assertThat(logger.getIncludeLocation(), is(false));
  }

  @Test
  public void getMarker() {
    JLogger logger = JLoggers.get(MARKER);
    assertThat(logger.getName(), is(JLoggersTest.class.getName()));
    assertThat(logger.getMarker(), is(MARKER));
    assertThat(logger.getIncludeLocation(), is(false));
  }

  @Test
  public void getMarkerLocation() {
    JLogger logger = JLoggers.get(MARKER, true);
    assertThat(logger.getName(), is(JLoggersTest.class.getName()));
    assertThat(logger.getMarker(), is(MARKER));
    assertThat(logger.getIncludeLocation(), is(true));
  }

}