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

package com.ealva.ealvalog.util

import org.junit.Before
import org.junit.Test

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

/**
 * Test some utility methods
 *
 *
 * Created by Eric A. Snell on 3/14/17.
 */
class LogUtilTest {

  private lateinit var plain: String
  private lateinit var anonInner: String
  private lateinit var inner: String
  private lateinit var inner2: String
  private lateinit var innerInner: String

  @Before
  fun setup() {
    plain = "com.test.OuterClass"
    anonInner = "com.test.OuterClass\$1"
    inner = "com.test.OuterClass\$InnerClass"
    inner2 = "com.test.OuterClass\$InnerClass2"
    innerInner = "com.test.OuterClass\$InnerClass\$InnerInnerClass"
  }

  @Test
  fun tesTagFromName() {
    val result = "OuterClass"

    assertThat(LogUtil.tagFromName(plain), `is`(equalTo(result)))
    assertThat(LogUtil.tagFromName(anonInner), `is`(equalTo(result)))
    assertThat(LogUtil.tagFromName(inner), `is`(equalTo(result)))
    assertThat(LogUtil.tagFromName(inner2), `is`(equalTo(result)))
    assertThat(LogUtil.tagFromName(innerInner), `is`(equalTo(result)))
  }

  @Test
  fun testStripInner() {
    val result = "com.test.OuterClass"

    assertThat(
      LogUtil.stripInnerClassesFromName(plain),
      `is`(equalTo(result))
    )
    assertThat(
      LogUtil.stripInnerClassesFromName(anonInner),
      `is`(equalTo(result))
    )
    assertThat(
      LogUtil.stripInnerClassesFromName(inner),
      `is`(equalTo(result))
    )
    assertThat(
      LogUtil.stripInnerClassesFromName(inner2),
      `is`(equalTo(result))
    )
    assertThat(LogUtil.stripInnerClassesFromName(innerInner), `is`(equalTo(result)))
  }

  @Test
  fun testCombine() {
    val nextToLast = "nextToLast"
    val last = "last"
    val first = "first"
    val second = "second"
    val desired = arrayOf<Any>(first, second, nextToLast, last)
    val actual = LogUtil.combineArgs(arrayOf<Any>(nextToLast, last), first, second)
    assertThat(actual, `is`(equalTo(desired)))
  }
}
