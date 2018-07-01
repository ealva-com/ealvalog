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

package com.ealva.ealvalog.core;

import com.ealva.ealvalog.util.LogMessageFormatter;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Eric A. Snell on 6/29/18.
 */
public interface LogRecordBuilder extends LogMessageFormatter {
  /**
   * Add the source location, determined by examining the call stack, to the log record. This is
   * an expensive operation as the JVM has to fill out the entire stack frame.
   *
   * @param stackDepth should typically be 0 to add the current location
   *
   * @return this LogRecordBuilder
   */
  @SuppressWarnings("unused")
  @NotNull LogRecordBuilder addLocation(int stackDepth);
}
