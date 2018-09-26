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

package com.ealva.ealvalog.log4j

import com.ealva.ealvalog.Markers
import com.ealva.ealvalog.e
import com.ealva.ealvalog.i
import com.ealva.ealvalog.invoke
import com.ealva.ealvalog.logger
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.config.Configurator
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by Eric A. Snell on 9/25/18.
 */
class Log4jLoggerAdapterTest {
  private lateinit var appender: Appender

  @Before
  fun setup() {
    appender = mock {
      on { isStarted }.doReturn(true)
      on { name }.doReturn("Appender")
    }
    val rootLogger = LogManager.getRootLogger() as Logger
    rootLogger.addAppender(appender)
    Log4jConfiguration().configure()
  }

  @After
  fun tearDown() {
    (LogManager.getLogger() as org.apache.logging.log4j.core.Logger).removeAppender(appender)
  }

  @Test
  fun testLog() {
    // Given
    val loggerName = "Test"
    val logger = logger(loggerName)
    val message = "Message"

    // When
    logger.e { it(message) }

    // Then
    val captor = argumentCaptor<LogEvent>()
    verify(appender, times(1)).append(captor.capture())
    val event = captor.firstValue
    expect(event.loggerName).toBe(loggerName)
    expect(event.loggerFqcn).toBe(Log4jLoggerAdapter::class.java.name)
    expect(event.level).toBe(Level.ERROR)
    expect(event.message.formattedMessage).toBe(message)
  }

  @Test
  fun testLogParams() {
    // Given
    val loggerName = "Test"
    val logger = logger(loggerName)
    val message = "Message"
    val arg = 1

    // When
    logger.e { it("$message {}", arg) }

    // Then
    val captor = argumentCaptor<LogEvent>()
    verify(appender, times(1)).append(captor.capture())
    val event = captor.firstValue
    expect(event.loggerName).toBe(loggerName)
    expect(event.loggerFqcn).toBe(Log4jLoggerAdapter::class.java.name)
    expect(event.level).toBe(Level.ERROR)
    expect(event.message.formattedMessage).toBe("$message $arg")
  }

  @Test
  fun testLogThrown() {
    // Given
    val loggerName = "Test"
    val logger = logger(loggerName)
    val message = "Message"
    val arg = 1
    val ex = RuntimeException("")

    // When
    logger.e(ex) { it("$message {}", arg) }

    // Then
    val captor = argumentCaptor<LogEvent>()
    verify(appender, times(1)).append(captor.capture())
    val event = captor.firstValue
    expect(event.loggerName).toBe(loggerName)
    expect(event.loggerFqcn).toBe(Log4jLoggerAdapter::class.java.name)
    expect(event.level).toBe(Level.ERROR)
    expect(event.message.formattedMessage).toBe("$message $arg")
    expect(event.thrown).toBe(ex)
    expect(event.thrownProxy.throwable).toBe(ex)
  }

  @Test
  fun testLogMarkerAndThrown() {
    // Given
    val loggerName = "Test"
    val logger = logger(loggerName)
    val message = "Message"
    val arg = 1
    val ex = RuntimeException("")
    val markerName = "MarkerName"
    val marker = Markers[markerName]

    // When
    logger.e(ex, marker) { it("$message {}", arg) }

    // Then
    val captor = argumentCaptor<LogEvent>()
    verify(appender, times(1)).append(captor.capture())
    val event = captor.firstValue
    expect(event.loggerName).toBe(loggerName)
    expect(event.loggerFqcn).toBe(Log4jLoggerAdapter::class.java.name)
    expect(event.level).toBe(Level.ERROR)
    expect(event.message.formattedMessage).toBe("$message $arg")
    expect(event.thrown).toBe(ex)
    expect(event.thrownProxy.throwable).toBe(ex)
    expect(event.marker).toBeInstanceOf<org.apache.logging.log4j.Marker>()
  }

  @Test
  fun testLogOtherInfo() {
    // Given
    val mills = System.currentTimeMillis()
    val nanos = System.nanoTime()
    val loggerName = "Test"
    val logger = logger(loggerName)
    val message = "Message"

    // When
    logger.e { it(message) }

    // Then
    val captor = argumentCaptor<LogEvent>()
    verify(appender, times(1)).append(captor.capture())
    val event = captor.firstValue
    expect(event.loggerName).toBe(loggerName)
    expect(event.loggerFqcn).toBe(Log4jLoggerAdapter::class.java.name)
    expect(event.level).toBe(Level.ERROR)
    expect(event.message.formattedMessage).toBe(message)
    val current = Thread.currentThread()
    expect(event.threadId).toBe(current.id)
    expect(event.threadPriority).toBe(current.priority)
    expect(event.timeMillis).toBeIn(mills..System.currentTimeMillis())
    expect(event.nanoTime).toBeIn(nanos..System.nanoTime())
  }

  @Test
  fun testSetLogLevel() {
    // Given
    val loggerName = "Test"
    val logger = logger(loggerName)
    val message = "Message"
    Configurator.setLevel(loggerName, Level.ERROR)

    // When
    logger.i { it(message) }

    // Then
    val captor = argumentCaptor<LogEvent>()
    verify(appender, never()).append(captor.capture())
  }

  @Test
  fun testMdcNdc() {
    // Given
    ThreadContext.clearAll()
    val key = "key"
    val value = "value"
    val stackValue = "StackVal"
    ThreadContext.put(key, value)
    ThreadContext.push(stackValue)

    val loggerName = "Test"
    val logger = logger(loggerName)
    val message = "Message"

    // When
    logger.e { it(message) }

    // Then
    val captor = argumentCaptor<LogEvent>()
    verify(appender, times(1)).append(captor.capture())
    val event = captor.firstValue
    expect(event.loggerName).toBe(loggerName)
    expect(event.loggerFqcn).toBe(Log4jLoggerAdapter::class.java.name)
    expect(event.level).toBe(Level.ERROR)
    expect(event.message.formattedMessage).toBe(message)

    val contextData = event.contextData
    expect(contextData).toNotBeNull()
    expect(contextData.isEmpty).toBe(false)
    expect(contextData.getValue<Any>(key)).toBe(value)
    val contextStack = event.contextStack
    expect(contextStack.depth).toBe(1)
    expect(contextStack.peek()).toBe(stackValue)
    expect(contextStack.pop()).toBe(stackValue)
    expect(contextStack.depth).toBe(0)
  }
}