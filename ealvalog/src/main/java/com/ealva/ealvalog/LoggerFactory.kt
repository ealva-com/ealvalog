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

/**
 * Creates [Logger] instances, typically named based on the class which will be
 * logging.
 *
 *  Created by Eric A. Snell on 2/28/17.
 */
interface LoggerFactory {

  /**
   * Get a Logger instance with the given [name] that always logs [marker]
   *
   * @param name   the name of the logger. This name will be treated as if it were a class name
   * with the canonical package hierarchy
   * @param marker optional [Marker] - every log from the returned [Logger] will use this as
   * it's marker unless overridden on a per method basis
   * @param includeLocation include call site location in every log call. Formatters must be
   * configured to display this optional information.
   *
   * @return [Logger] instance
   */
  fun get(name: String, marker: Marker?, includeLocation: Boolean): Logger

  fun get(name: String): Logger

  companion object {
    const val ROOT_LOGGER_NAME = ""
  }
}
