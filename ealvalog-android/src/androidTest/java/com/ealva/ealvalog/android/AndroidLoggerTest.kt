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

import android.util.Log
import androidx.test.runner.AndroidJUnit4
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Loggers
import com.ealva.ealvalog.Markers
import com.ealva.ealvalog.core.ExtLogRecord
import com.ealva.ealvalog.java.JLoggers
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.RuntimeException

/**
 * Created by Eric A. Snell on 9/29/18.
 */
@RunWith(AndroidJUnit4::class)
class AndroidLoggerTest {
  @Before
  fun setup() {
    AndroidLoggerFactory.clearLoggerCache()
    Loggers.setFactory(AndroidLoggerFactory)
  }

  @Test
  fun testPrintLog() {
    // Given
    val mockHandler = mock<LogHandler> {
      on { isLoggable(TAG, LogLevel.CRITICAL, null, null) }.thenReturn(true)
      on { shouldIncludeLocation(TAG, Log.ASSERT, null, null) }.thenReturn(false)
    }
    AndroidLogger.setHandler(mockHandler)
    val logger = JLoggers.get(AndroidLoggerTest::class.java)
    val msg = "The msg"

    // When
    logger.log(LogLevel.CRITICAL, msg)

    // Then
    val recordCaptor = argumentCaptor<ExtLogRecord>()
    verify(mockHandler, times(1)).prepareLog(recordCaptor.capture())
    val record = recordCaptor.firstValue
    expect(record.loggerName).toBe(AndroidLoggerTest::class.java.name)
    expect(record.logLevel).toBe(LogLevel.CRITICAL)
    expect(record.message).toBe(msg)
    expect(record.thrown).toBeNull()
    expect(record.marker).toBeNull()
    expect(record.location).toBeNull()
  }

  @Test
  fun testPrintLogHandlerWantsLocation() {
    // Given
    val ex = RuntimeException("MyException")
    val mockHandler = mock<LogHandler> {
      on { isLoggable(TAG, LogLevel.WARN, null, ex) }.thenReturn(true)
      on { shouldIncludeLocation(TAG, Log.WARN, null, ex) }.thenReturn(true)
    }
    AndroidLogger.setHandler(mockHandler)
    val logger = JLoggers.get(AndroidLoggerTest::class.java)
    val msg = "The msg"

    // When
    logger.log(LogLevel.WARN, ex, msg)

    // Then
    val recordCaptor = argumentCaptor<ExtLogRecord>()
    verify(mockHandler, times(1)).prepareLog(recordCaptor.capture())
    val record = recordCaptor.firstValue
    expect(record.loggerName).toBe(AndroidLoggerTest::class.java.name)
    expect(record.logLevel).toBe(LogLevel.WARN)
    expect(record.message).toBe(msg)
    expect(record.thrown).toBe(ex)
    expect(record.marker).toBeNull()
    expect(record.location).toNotBeNull()
  }

  @Test
  fun testPrintLogSetIncludeLocation() {
    // Given
    val marker = Markers["MarkerName"]
    val mockHandler = mock<LogHandler> {
      on { isLoggable(TAG, LogLevel.ERROR, marker, null) }.thenReturn(true)
      on { shouldIncludeLocation(TAG, Log.ERROR, marker, null) }.thenReturn(false)
    }
    AndroidLogger.setHandler(mockHandler)
    val logger = JLoggers.get(AndroidLoggerTest::class.java)
    logger.includeLocation = true
    val msg = "The msg"

    // When
    logger.log(LogLevel.ERROR, marker, msg)

    // Then
    val recordCaptor = argumentCaptor<ExtLogRecord>()
    verify(mockHandler, times(1)).prepareLog(recordCaptor.capture())
    val record = recordCaptor.firstValue
    expect(record.loggerName).toBe(AndroidLoggerTest::class.java.name)
    expect(record.logLevel).toBe(LogLevel.ERROR)
    expect(record.message).toBe(msg)
    expect(record.thrown).toBeNull()
    expect(record.marker).toBe(marker)
    expect(record.location).toNotBeNull()
  }

  companion object {
    const val TAG = "AndroidLoggerTest"
  }
}
