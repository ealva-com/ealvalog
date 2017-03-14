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

package ealvalog.util;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Test some utility methods
 *
 * Created by Eric A. Snell on 3/14/17.
 */
public class LogUtilTest {

  private String plain;
  private String anonInner;
  private String inner;
  private String inner2;
  private String innerInner;

  @Before
  public void setup() {
    plain = "com.test.OuterClass";
    anonInner = "com.test.OuterClass$1";
    inner = "com.test.OuterClass$InnerClass";
    inner2 = "com.test.OuterClass$InnerClass2";
    innerInner = "com.test.OuterClass$InnerClass$InnerInnerClass";
  }

  @Test
  public void tesTagFromName() throws Exception {
    final String result = "OuterClass";

    assertThat(LogUtil.tagFromName(plain), is(equalTo(result)));
    assertThat(LogUtil.tagFromName(anonInner), is(equalTo(result)));
    assertThat(LogUtil.tagFromName(inner), is(equalTo(result)));
    assertThat(LogUtil.tagFromName(inner2), is(equalTo(result)));
    assertThat(LogUtil.tagFromName(innerInner), is(equalTo(result)));
  }

  @Test
  public void testStripInner() throws Exception {
    final String result = "com.test.OuterClass";

    assertThat(LogUtil.stripInnerClassesFromName(plain), is(equalTo(result)));
    assertThat(LogUtil.stripInnerClassesFromName(anonInner), is(equalTo(result)));
    assertThat(LogUtil.stripInnerClassesFromName(inner), is(equalTo(result)));
    assertThat(LogUtil.stripInnerClassesFromName(inner2), is(equalTo(result)));
    assertThat(LogUtil.stripInnerClassesFromName(innerInner), is(equalTo(result)));
  }
}
