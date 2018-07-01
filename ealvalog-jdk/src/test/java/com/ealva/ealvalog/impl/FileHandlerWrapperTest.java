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

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.core.ExtLogRecord;
import com.ealva.ealvalog.impl.FileHandlerWrapper;
import com.ealva.ealvalog.impl.HandlerWrapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Test to see if the handle is configured correctly
 * <p>
 * Created by Eric A. Snell on 3/8/17.
 */
public class FileHandlerWrapperTest {

  private static final String MSG = "msg";
  private ExtLogRecord record;

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void setup() {
    record = ExtLogRecord.get(LogLevel.CRITICAL,
                              MSG,
                              "LoggerName",
                              null,
                              null);
  }

  @Test(expected = IllegalStateException.class)
  public void testNoPattern() throws Exception {
    FileHandlerWrapper.Builder builder = FileHandlerWrapper.builder();
    //noinspection unused
    final HandlerWrapper handler = builder.build();
  }

  @Test
  public void testWithPattern() throws Exception {
    final File root = folder.getRoot();
    final String fileNamePattern = new File(root, "ealvalog.%g.%u.log").getAbsolutePath();
    final HandlerWrapper handler = FileHandlerWrapper.builder().fileNamePattern(fileNamePattern)
                                                                   .build();
    handler.publish(record);

    final String[] array = root.list();
    assertThat(array, is(notNullValue()));

    final List<String> list = Arrays.asList(array);

    //noinspection ConstantConditions
    assertThat(list, is(hasItem("ealvalog.0.0.log")));
  }

}
