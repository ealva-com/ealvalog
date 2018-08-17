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

package com.ealva.ealvalog

import java.util.logging.Filter

/**
 * Used at top layer of logging to prevent unnecessary calls into lower layers, hence preventing
 * lots of unnecessary objects being created
 *
 * Created by Eric A. Snell on 3/6/17.
 */
interface LoggerFilter : Filter {

  /**
   * Will a log at this [LogLevel], with the given (optional) [Marker] and (optional) [Throwable],
   * result in an actual log statement
   *
   * @param loggerName    the logger that is making the log call
   * @param logLevel     the level to test
   * @param marker    optional marker to test
   * @param throwable optional throwable to test
   *
   * @return [FilterResult.ACCEPT] or [FilterResult.NEUTRAL] if logging will proceed
   */
  fun isLoggable(
    loggerName: String,
    logLevel: LogLevel,
    marker: Marker?,
    throwable: Throwable?
  ): FilterResult
}
