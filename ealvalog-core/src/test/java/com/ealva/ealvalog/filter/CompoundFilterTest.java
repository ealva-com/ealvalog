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

import com.ealva.ealvalog.ExtLogRecord;
import com.ealva.ealvalog.FilterResult;
import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.LoggerFilter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;

/**
 * Created by Eric A. Snell on 8/17/18.
 */
public class CompoundFilterTest {
    private static final String LOGGER_NAME = "com.ealva.logger.LoggerName";
    private static final LogLevel LOG_LEVEL = LogLevel.ERROR;
    @Mock LoggerFilter accept;
    @Mock LoggerFilter neutral;
    @Mock LoggerFilter deny;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(accept.isLoggable(
            LOGGER_NAME,
            LogLevel.ERROR,
            null,
            null
             )
        ).thenReturn(FilterResult.ACCEPT);
        when(neutral.isLoggable(
            LOGGER_NAME,
            LogLevel.ERROR,
            null,
            null
             )
        ).thenReturn(FilterResult.NEUTRAL);
        when(deny.isLoggable(
            LOGGER_NAME,
            LogLevel.ERROR,
            null,
            null
             )
        ).thenReturn(FilterResult.DENY);
    }

    @Test
    public void testAdd() {
        CompoundFilter filter = new CompoundFilter(AlwaysNeutralFilter.INSTANCE);
        assertThat(filter.add(AlwaysAcceptFilter.INSTANCE), is(true));
        assertThat(filter.add(AlwaysAcceptFilter.INSTANCE), is(false));
        assertThat(filter.add(AlwaysNeutralFilter.INSTANCE), is(false));
    }

    @Test
    public void testRemove() {
        CompoundFilter filter = new CompoundFilter(AlwaysNeutralFilter.INSTANCE);
        assertThat(filter.remove(AlwaysAcceptFilter.INSTANCE), is(false));
        assertThat(filter.remove(AlwaysNeutralFilter.INSTANCE), is(true));
        assertThat(filter.remove(AlwaysNeutralFilter.INSTANCE), is(false));
    }

    @Test
    public void testClear() {
        CompoundFilter
            filter =
            new CompoundFilter(AlwaysNeutralFilter.INSTANCE, AlwaysAcceptFilter.INSTANCE);
        filter.clear();
        assertThat(filter.remove(AlwaysAcceptFilter.INSTANCE), is(false));
        assertThat(filter.remove(AlwaysNeutralFilter.INSTANCE), is(false));
    }

    @Test
    public void testAddAll() {
        CompoundFilter filter = new CompoundFilter();
        filter.addAll(AlwaysNeutralFilter.INSTANCE, AlwaysAcceptFilter.INSTANCE);
        assertThat(filter.add(AlwaysNeutralFilter.INSTANCE), is(false));
        assertThat(filter.add(AlwaysAcceptFilter.INSTANCE), is(false));
        assertThat(filter.remove(AlwaysAcceptFilter.INSTANCE), is(true));
        assertThat(filter.remove(AlwaysNeutralFilter.INSTANCE), is(true));
    }

    @Test
    public void testIsLoggableSingleFilter() {
        CompoundFilter filter = new CompoundFilter(neutral);
        try (ExtLogRecord record = ExtLogRecord.get(LOG_LEVEL,
                                                    LOGGER_NAME,
                                                    null,
                                                    null)) {
            assertThat(filter.isLoggable(record), is(true));
            then(neutral).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
        }

        filter = new CompoundFilter(accept);
        try (ExtLogRecord record = ExtLogRecord.get(LOG_LEVEL,
                                                    LOGGER_NAME,
                                                    null,
                                                    null)) {
            assertThat(filter.isLoggable(record), is(true));
            then(accept).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
        }

        filter = new CompoundFilter(deny);
        try (ExtLogRecord record = ExtLogRecord.get(LOG_LEVEL,
                                                    LOGGER_NAME,
                                                    null,
                                                    null)) {
            assertThat(filter.isLoggable(record), is(false));
            then(deny).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
        }
    }

    @Test
    public void testIsLoggableNeutralAccept() {
        CompoundFilter filter = new CompoundFilter(neutral, accept);
        try (ExtLogRecord record = ExtLogRecord.get(LOG_LEVEL,
                                                    LOGGER_NAME,
                                                    null,
                                                    null)) {
            assertThat(filter.isLoggable(record), is(true));
            then(neutral).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
            then(accept).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
        }
    }

    @Test
    public void testIsLoggableNeutralDeny() {
        CompoundFilter filter = new CompoundFilter(neutral, deny);
        try (ExtLogRecord record = ExtLogRecord.get(LOG_LEVEL,
                                                    LOGGER_NAME,
                                                    null,
                                                    null)) {
            assertThat(filter.isLoggable(record), is(false));
            then(neutral).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
            then(deny).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
        }
    }

    @Test
    public void testIsLoggableDenyNeutral() {
        // deny first should short-circuit
        CompoundFilter filter = new CompoundFilter(deny, neutral);
        try (ExtLogRecord record = ExtLogRecord.get(LOG_LEVEL,
                                                    LOGGER_NAME,
                                                    null,
                                                    null)) {
            assertThat(filter.isLoggable(record), is(false));
            then(deny).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
            then(neutral).should(never()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
        }
    }

    @Test
    public void testIsLoggableAcceptNeutral() {
        CompoundFilter filter = new CompoundFilter(accept, neutral);
        try (ExtLogRecord record = ExtLogRecord.get(LOG_LEVEL,
                                                    LOGGER_NAME,
                                                    null,
                                                    null)) {
            assertThat(filter.isLoggable(record), is(true));
            then(accept).should(only()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
            then(neutral).should(never()).isLoggable(LOGGER_NAME, LOG_LEVEL, null, null);
        }
    }

}
