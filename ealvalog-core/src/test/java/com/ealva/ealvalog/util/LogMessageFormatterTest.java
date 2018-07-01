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

package com.ealva.ealvalog.util;

import com.ealva.ealvalog.util.LogMessageFormatter;
import com.ealva.ealvalog.util.LogMessageFormatterImpl;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Ensure MessageFormatter correctly handles the internal StringBuilder
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
public class LogMessageFormatterTest {
  private static final String FORMAT = "%s";
  private static final String ARG = "Arrrrgh";
  private static final String RESULT = ARG;
  private static final String PREFIX = "Prefixin's";
  private static final String SUFFIX = "Suffixilicous";

  @Test
  public void testFormat() {
    LogMessageFormatter format = new com.ealva.ealvalog.util.LogMessageFormatterImpl();
    assertThat(format.append(FORMAT, ARG).toString(), is(equalTo(RESULT)));
  }

  @Test
  public void testBuilderUse() {
    com.ealva.ealvalog.util.LogMessageFormatterImpl
        format = new com.ealva.ealvalog.util.LogMessageFormatterImpl();
    format.append(PREFIX);
    format.append(FORMAT, ARG);
    format.append(SUFFIX);
    assertThat(format.toString(), is(equalTo(PREFIX + RESULT + SUFFIX)));
  }

  @Test
  public void testReset() {
    com.ealva.ealvalog.util.LogMessageFormatterImpl
        format = new com.ealva.ealvalog.util.LogMessageFormatterImpl();
    format.append(PREFIX);
    assertThat(format.toString(), is(equalTo(PREFIX)));
    format.reset();
    format.append(FORMAT, ARG);
    assertThat(format.toString(), is(equalTo(RESULT)));
    format.reset();
    format.append(SUFFIX);
    assertThat(format.toString(), is(equalTo(SUFFIX)));
  }

  @Test
  public void testMultipleToString() {
    com.ealva.ealvalog.util.LogMessageFormatterImpl format = new LogMessageFormatterImpl();
    format.append(FORMAT, ARG);
    assertThat(format.toString(), is(equalTo(RESULT)));
    assertThat(format.toString(), is(equalTo(RESULT)));
    format.append(SUFFIX);
    assertThat(format.toString(), is(equalTo(RESULT + SUFFIX)));
    assertThat(format.toString(), is(equalTo(RESULT + SUFFIX)));
    format.reset();
    assertThat(format.toString(), is(equalTo("")));
    assertThat(format.toString(), is(equalTo("")));
    format.append(FORMAT, ARG);
    assertThat(format.toString(), is(equalTo(RESULT)));
    assertThat(format.toString(), is(equalTo(RESULT)));
  }


}
