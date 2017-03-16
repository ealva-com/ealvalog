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
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.FormattableFlags;
import java.util.Formatter;
import java.util.Locale;

/**
 * Test various formatting methods
 * <p>
 * Created by Eric A. Snell on 3/15/17.
 */
public class FormattableStackTraceElementTest {
  private static final String DECLARING_CLASS = "DeclaringClass";
  private static final String METHOD_NAME = "MethodName";
  private static final String FILE_NAME_JAVA = "FileName.java";
  private static final int LINE_NUMBER = 100;

  private static final String EXPECTED = String.format(Locale.getDefault(), "(%s:%d)", METHOD_NAME, LINE_NUMBER);
  private static final String EXPECTED_ALT = String.format(Locale.getDefault(), "(%s.%s:%d)", DECLARING_CLASS, METHOD_NAME, LINE_NUMBER);

  private Formatter formatter;
  private StackTraceElement location;

  @Before
  public void setup() {
    formatter = new Formatter();
    location = new StackTraceElement(DECLARING_CLASS, METHOD_NAME, FILE_NAME_JAVA, LINE_NUMBER);
  }

  @Test
  public void testNull() {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(null);
    formattable.formatTo(formatter, 0, -1, -1);
    assertThat(formatter.toString(), is(equalTo("")));
  }

  @Test
  public void formatTo() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, 0, -1, -1);
    assertThat(formatter.toString(), is(equalTo(EXPECTED)));
  }

  @Test
  public void formatToAlt() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, FormattableFlags.ALTERNATE, -1, -1);
    assertThat(formatter.toString(), is(equalTo(EXPECTED_ALT)));
  }

  @Test
  public void formatToPrecision() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, 0, -1, 10);
    assertThat(formatter.toString(), is(equalTo("(…ame:100)")));
  }

  @Test
  public void formatAltToPrecision() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, FormattableFlags.ALTERNATE, -1, 24);
    assertThat(formatter.toString(), is(equalTo("(…gClass.MethodName:100)")));
  }

  @Test
  public void formatToWidth() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, 0, EXPECTED.length() + 5, -1);
    assertThat(formatter.toString(), is(equalTo("     " + EXPECTED )));
  }

  @Test
  public void formatToWidthLeftJustified() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, FormattableFlags.LEFT_JUSTIFY, EXPECTED.length() + 5, -1);
    assertThat(formatter.toString(), is(equalTo(EXPECTED + "     ")));
  }

  @Test
  public void formatToPrecisionLargerWidth() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, 0, 12, 10);
    assertThat(formatter.toString(), is(equalTo("  (…ame:100)")));
  }

  @Test
  public void formatAltToPrecisionLargerWidth() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, FormattableFlags.ALTERNATE, 26, 24);
    assertThat(formatter.toString(), is(equalTo("  (…gClass.MethodName:100)")));
  }

  @Test
  public void formatToPrecisionLargerWidthLeftJustifited() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, FormattableFlags.LEFT_JUSTIFY, 12, 10);
    assertThat(formatter.toString(), is(equalTo("(…ame:100)  ")));
  }

  @Test
  public void formatAltToPrecisionLargerWidthLeftJustifited() throws Exception {
    FormattableStackTraceElement formattable = FormattableStackTraceElement.make(location);
    formattable.formatTo(formatter, FormattableFlags.LEFT_JUSTIFY | FormattableFlags.ALTERNATE, 26, 24);
    assertThat(formatter.toString(), is(equalTo("(…gClass.MethodName:100)  ")));
  }

}
