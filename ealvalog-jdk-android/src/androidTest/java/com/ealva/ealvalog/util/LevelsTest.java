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

import com.ealva.ealvalog.LogLevel;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import android.util.Log;

/**
 * Created by Eric A. Snell on 8/15/18.
 */
public class LevelsTest {
    @Test
    public void testLevelMapping() {
        assertThat(Levels.INSTANCE.toAndroidLevel(LogLevel.TRACE), is(Log.VERBOSE));
        assertThat(Levels.INSTANCE.toAndroidLevel(LogLevel.DEBUG), is(Log.DEBUG));
        assertThat(Levels.INSTANCE.toAndroidLevel(LogLevel.INFO), is(Log.INFO));
        assertThat(Levels.INSTANCE.toAndroidLevel(LogLevel.WARN), is(Log.WARN));
        assertThat(Levels.INSTANCE.toAndroidLevel(LogLevel.ERROR), is(Log.ERROR));
        assertThat(Levels.INSTANCE.toAndroidLevel(LogLevel.CRITICAL), is(Log.ASSERT));
    }

}
