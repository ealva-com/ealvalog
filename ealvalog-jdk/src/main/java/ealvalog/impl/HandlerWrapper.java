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

package ealvalog.impl;

import ealvalog.FilterResult;
import ealvalog.LogLevel;
import ealvalog.Logger;
import ealvalog.LoggerFilter;
import ealvalog.Marker;
import ealvalog.NullMarker;
import ealvalog.Loggers;
import ealvalog.util.NullThrowable;
import org.jetbrains.annotations.NotNull;

import static ealvalog.FilterResult.ACCEPT;
import static ealvalog.FilterResult.DENY;
import static ealvalog.FilterResult.NEUTRAL;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Wrap a JUL handler
 * <p>
 * Created by Eric A. Snell on 3/8/17.
 */
@SuppressWarnings("unused")
public class HandlerWrapper extends BaseLoggerHandler {
  public static FileHandlerBuilder fileBuilder() {
    return new FileHandlerBuilder();
  }

  public static ConsoleHandlerBuilder consoleBuilder() {
    return new ConsoleHandlerBuilder();
  }

  private final Handler realHandler;

  HandlerWrapper(final @NotNull Handler realHandler, final @NotNull LoggerFilter filter) {
    super(filter);
    this.realHandler = realHandler;
  }

  @Override
  public FilterResult isLoggable(@NotNull final Logger logger,
                                 @NotNull final LogLevel level,
                                 @NotNull final Marker marker,
                                 @NotNull final Throwable throwable) {
    final int levelValue = realHandler.getLevel().intValue();
    if (level.getJdkLevel().intValue() < levelValue || levelValue == Level.OFF.intValue()) {
      return DENY;
    }
    final FilterResult filterResult = getLoggerFilter().isLoggable(logger, level, marker, throwable);
    if (filterResult != NEUTRAL) {
      return filterResult;
    }
    return ACCEPT;
  }

  @Override public void publish(final LogRecord record) {
    if (isLoggable(Loggers.get(record.getLoggerName()),
                   LogLevel.fromLevel(record.getLevel()),
                   getRecordMarker(record),
                   NullThrowable.nullToNullInstance(record.getThrown())) != DENY) {
      realHandler.publish(record);
    }
  }

  private Marker getRecordMarker(final LogRecord record) {
    if (record instanceof ExtLogRecord) {
      return ((ExtLogRecord)record).getMarker();
    }
    return NullMarker.INSTANCE;
  }

  @Override public void flush() {
    realHandler.flush();
  }

  @Override public void close() throws SecurityException {
    realHandler.close();
  }

}
