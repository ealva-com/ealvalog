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

package com.ealva.ealvalog.filter;

import com.ealva.ealvalog.FilterResult;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.filter.MarkerFilter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Basic tests
 * <p>
 * Created by Eric A. Snell on 3/17/17.
 */
public class MarkerFilterTest {
  @SuppressWarnings("WeakerAccess") @Mock Logger logger;
  @SuppressWarnings("WeakerAccess") @Mock Marker marker;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testBuilder() throws Exception {
    when(marker.isOrContains(marker)).thenReturn(true);
    final com.ealva.ealvalog.filter.MarkerFilter filter = MarkerFilter.Companion.builder()
                                                                                .marker(marker)
                                                                                .build();
    assertThat(filter.isLoggable(logger, LogLevel.CRITICAL, marker, null), is(FilterResult.NEUTRAL));
  }

  @Test(expected = IllegalStateException.class)
  public void testBuilderNoMarker() throws Exception {
    //noinspection unused
    final com.ealva.ealvalog.filter.MarkerFilter filter = MarkerFilter.Companion.builder()
                                                                                .build();
  }


  @Test
  public void testMarkerDoesNotMatch() throws Exception {
    when(marker.isOrContains(marker)).thenReturn(false);
    final com.ealva.ealvalog.filter.MarkerFilter filter = MarkerFilter.Companion.builder()
                                                                                .marker(marker)
                                                                                .build();
    assertThat(filter.isLoggable(logger, LogLevel.CRITICAL, marker, null), is(FilterResult.DENY));
  }
}
