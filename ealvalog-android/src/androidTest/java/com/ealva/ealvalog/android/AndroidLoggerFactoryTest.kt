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

package com.ealva.ealvalog.android

import android.support.test.runner.AndroidJUnit4
import com.ealva.ealvalog.Loggers
import com.ealva.ealvalog.NullLoggerFactory
import com.ealva.ealvalog.android.AndroidLoggerFactory
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Eric A. Snell on 8/8/18.
 */
@RunWith(AndroidJUnit4::class)
class AndroidLoggerFactoryTest {
  @Before
  fun setup() {
    Loggers.setFactory(AndroidLoggerFactory)
  }

  @After
  fun tearDown() {
    Loggers.setFactory(NullLoggerFactory)
  }

  @Test
  fun testGetRootLogger() {
    val name = "Test"
    val logger = Loggers.get(name)
    assertThat(logger, sameInstance(Loggers.get(name)))
  }
}
