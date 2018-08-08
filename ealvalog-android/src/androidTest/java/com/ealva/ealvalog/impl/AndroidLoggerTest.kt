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

package com.ealva.ealvalog.impl

import android.support.test.runner.AndroidJUnit4
import android.util.Log
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Loggers
import com.ealva.ealvalog.Marker
import com.ealva.ealvalog.util.LogMessageFormatterImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.ArgumentMatchers.same
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeastOnce
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class AndroidLoggerTest {
  @Mock internal var mockHandler: LogHandler? = null

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    AndroidLogger.setHandler(mockHandler!!)
    Loggers.setFactory(AndroidLoggerFactory)
  }

  @Test
  fun testPrintLog() {
    `when`(mockHandler!!.isLoggable(TAG, Log.ASSERT)).thenReturn(true)

    //given
    val logger = Loggers.get()
    val msg = "The msg"
    logger.log(LogLevel.CRITICAL, msg)

    then<LogHandler>(mockHandler).should(atLeastOnce()).prepareLog(
      eq("AndroidLoggerTest"),
      same(Log.ASSERT),
      isNull(Marker::class.java),
      isNull(Throwable::class.java),
      isNull(StackTraceElement::class.java),
      any(LogMessageFormatterImpl::class.java),
      eq(msg)
    )
  }

  @Test
  fun testPrintLogHandlerWantsLocation() {
    `when`(mockHandler!!.shouldIncludeLocation(TAG, Log.ASSERT, null, null)).thenReturn(true)
    `when`(mockHandler!!.isLoggable(TAG, Log.ASSERT)).thenReturn(true)

    //given
    val logger = Loggers.get()
    val msg = "The msg"
    logger.log(LogLevel.CRITICAL, msg)

    then<LogHandler>(mockHandler).should(atLeastOnce()).prepareLog(
      eq("AndroidLoggerTest"),
      same(Log.ASSERT),
      isNull(Marker::class.java),
      isNull(Throwable::class.java),
      any(StackTraceElement::class.java),
      any(LogMessageFormatterImpl::class.java),
      eq(msg)
    )
  }

  @Test
  fun testPrintLogSetIncludeLocation() {
    `when`(mockHandler!!.isLoggable(TAG, Log.ASSERT)).thenReturn(true)

    //given
    val logger = Loggers.get()
    logger.includeLocation = true
    val msg = "The msg"
    logger.log(LogLevel.CRITICAL, msg)

    then<LogHandler>(mockHandler).should(atLeastOnce()).prepareLog(
      eq("AndroidLoggerTest"),
      same(Log.ASSERT),
      isNull(Marker::class.java),
      isNull(Throwable::class.java),
      any(StackTraceElement::class.java),
      any(LogMessageFormatterImpl::class.java),
      eq(msg)
    )
  }

  companion object {
    private const val TAG = "AndroidLoggerTest"
  }

}
