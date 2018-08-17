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

import com.ealva.ealvalog.ExtLogRecord;
import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.core.ExtRecordFormatter;
import com.ealva.ealvalog.filter.AlwaysAcceptFilter;
import com.ealva.ealvalog.filter.AlwaysNeutralFilter;
import com.ealva.ealvalog.util.LogUtil;

import static com.ealva.ealvalog.LogLevel.CRITICAL;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.support.test.runner.AndroidJUnit4;

import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

@RunWith(AndroidJUnit4.class)
public class AndroidLoggerHandlerTest {
  private AndroidLoggerHandlerForTest handler;

  @Before
  public void setup() {
    handler =
        new AndroidLoggerHandlerForTest(new ExtRecordFormatter("%1$s",
                                                               true),
                                        AlwaysNeutralFilter.INSTANCE,
                                        new ErrorManager());
  }

  @Test
  public void testIsLoggableNoFilter() {
    final String className = "com.ealva.ealvalog.impl.AndroidLoggerHandlerTest";
    try (final ExtLogRecord record = ExtLogRecord.get(CRITICAL, className, null, null)) {
      final String message = "Message";
      record.append(message);
      handler.publish(record);

      assertThat(handler.record, is((LogRecord)record));
      assertThat(handler.tag, is(LogUtil.tagFromName(className)));
      assertThat(handler.msg, is(message));
    }
  }

  @Test
  public void testIsLoggableAlwaysAcceptFilter() {
//    when(logger.getName()).thenReturn(AndroidLoggerHandlerTest.class.getName());
//
//    AndroidLoggerHandler handler = new AndroidLoggerHandlerForTest(
//        new ExtRecordFormatter(ExtRecordFormatter.TYPICAL_ANDROID_FORMAT, true),
//        AlwaysAcceptFilter.INSTANCE,
//        new ErrorManager()
//    );
//    assertThat(handler.isLoggable(logger.getName(), CRITICAL, NullMarker.INSTANCE,
//                                  NullThrowable.INSTANCE),
//               is(FilterResult.ACCEPT));
  }

  @Test
  public void testLogOutput() {
    AndroidLoggerHandler handler = AndroidLoggerHandler.Companion.make(
        new ExtRecordFormatter(ExtRecordFormatter.TYPICAL_ANDROID_FORMAT, true),
        AlwaysAcceptFilter.INSTANCE
    );
    try (final ExtLogRecord record = ExtLogRecord.get(CRITICAL, getClass().getName(), null, null)) {
      record.append("Message");
      handler.publish(record);
    }

  }

  class AndroidLoggerHandlerForTest extends AndroidLoggerHandler {
    @Nullable LogRecord record;
    @Nullable String tag;
    @Nullable String msg;

    AndroidLoggerHandlerForTest(@NotNull final Formatter aFormatter,
                                @NotNull final LoggerFilter loggerFilter,
                                @NotNull final ErrorManager anErrorMgr) {
      super(aFormatter, loggerFilter, anErrorMgr);
    }

    @Override
    protected void log(@NotNull final LogRecord record,
                       @NotNull final String tag,
                       @NotNull final String msg) {
      this.record = record;
      this.tag = tag;
      this.msg = msg;
    }
  }
}

