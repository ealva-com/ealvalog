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

package com.example.ealvatag_android;


import ealvalog.TheLoggerFactory;
import ealvalog.impl.AndroidLogger;
import ealvalog.impl.AndroidLoggerFactory;
import ealvalog.impl.DebugLogHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AndroidLoggerTest {
  @Before
  public void setup() {
    TheLoggerFactory.setFactory(new AndroidLoggerFactory());
  }

  @Test
  public void testDebugHandler() {
    AndroidLogger.setHandler(new DebugLogHandler());
  }
}
