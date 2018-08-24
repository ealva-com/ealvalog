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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Test the formatting using various options
 * <p>
 * Created by Eric A. Snell on 3/15/17.
 */
@SuppressWarnings("ThrowableNotThrown")
public class FormattableThrowableTest {

  private Formatter formatter;

  @Before
  public void setup() {
    formatter = new Formatter();
  }

  @Test
  public void testNull() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable = com.ealva.ealvalog.util.FormattableThrowable.make(null);
    formattableThrowable.formatTo(formatter, 0, -1, -1);
    assertThat(formatter.toString(), is(equalTo("")));
  }

  @Test
  public void testFormat() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable =
        com.ealva.ealvalog.util.FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, 0, -1, -1);
    assertThat(formatter.toString(), is(equalTo("java.lang.IllegalArgumentException: Blah")));
  }

  @Test
  public void testFormatUppercase() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable =
        com.ealva.ealvalog.util.FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, FormattableFlags.UPPERCASE, -1, -1);
    assertThat(formatter.toString(),
               is(equalTo("java.lang.IllegalArgumentException: Blah".toUpperCase())));
  }

  @Test
  public void testSmallPrecision() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable =
        com.ealva.ealvalog.util.FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, 0, -1, 18);
    assertThat(formatter.toString(), is(equalTo("java.lang.Illegal…")));
  }

  @Test
  public void testLargePrecision() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable =
        com.ealva.ealvalog.util.FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, 0, -1, 500);
    assertThat(formatter.toString(), is(equalTo("java.lang.IllegalArgumentException: Blah")));
  }

  @Test
  public void testWitdth() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable =
        com.ealva.ealvalog.util.FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, 0, 50, -1);
    assertThat(formatter.toString(),
               is(equalTo("          java.lang.IllegalArgumentException: Blah")));
  }

  @Test
  public void testWitdthLeftJustified() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable =
        com.ealva.ealvalog.util.FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, FormattableFlags.LEFT_JUSTIFY, 50, -1);
    assertThat(formatter.toString(),
               is(equalTo("java.lang.IllegalArgumentException: Blah          ")));
  }

  @Test
  public void testSmallPrecisionWithWidthLeftJustified() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable =
        com.ealva.ealvalog.util.FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, FormattableFlags.LEFT_JUSTIFY, 20, 18);
    assertThat(formatter.toString(), is(equalTo("java.lang.Illegal…  ")));
  }

  @Test
  public void testSmallPrecisionWithWidthUpperCase() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable =
        com.ealva.ealvalog.util.FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, FormattableFlags.UPPERCASE, 20, 18);
    assertThat(formatter.toString(), is(equalTo("  java.lang.Illegal…".toUpperCase())));
  }

  @Test
  public void testFormatAlternate() {
    com.ealva.ealvalog.util.FormattableThrowable
        formattableThrowable = FormattableThrowable.make(new IllegalArgumentException("Blah"));
    formattableThrowable.formatTo(formatter, FormattableFlags.ALTERNATE, -1, -1);
    final String result = formatter.toString();
    assertThat(result, is(startsWith("java.lang.IllegalArgumentException: Blah")));

    // ensure larger than non-alternative
    assertThat(result.length(),
               is(greaterThan("java.lang.IllegalArgumentException: Blah".length() + 20)));
  }

}
