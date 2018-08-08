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

package com.ealva.ealvalog.impl

import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.core.ExtRecordFormatter
import com.ealva.ealvalog.filter.AlwaysNeutralFilter

import java.io.IOException
import java.util.logging.ConsoleHandler
import java.util.logging.ErrorManager
import java.util.logging.FileHandler
import java.util.logging.Handler

/**
 * Wraps a ConsoleHandler and provides a builder
 *
 * Created by Eric A. Snell on 3/15/17.
 */
class ConsoleHandlerWrapper internal constructor(realHandler: Handler, filter: LoggerFilter) :
  HandlerWrapper(realHandler, filter) {

  /**
   * Build a [FileHandler] including an [ExtRecordFormatter]
   *
   * Created by Eric A. Snell on 3/8/17.
   */
  class Builder internal constructor() {
    private var formatterPattern: String
    private var formatterLogErrors: Boolean = false
    private var filter: LoggerFilter
    private var errorManager: ErrorManager

    init {
      formatterPattern = ExtRecordFormatter.TYPICAL_FORMAT
      formatterLogErrors = true
      filter = AlwaysNeutralFilter
      errorManager = ErrorManager()
    }

    fun extRecordFormatterPattern(pattern: String): Builder {
      this.formatterPattern = pattern
      return this
    }

    fun formatterLogErrors(logErrors: Boolean): Builder {
      this.formatterLogErrors = logErrors
      return this
    }

    fun filter(filter: LoggerFilter): Builder {
      this.filter = filter
      return this
    }

    fun errorManager(errorManager: ErrorManager): Builder {
      this.errorManager = errorManager
      return this
    }

    @Throws(IOException::class, IllegalStateException::class)
    fun build(): ConsoleHandlerWrapper {
      val confileHandler = ConsoleHandler()
      confileHandler.errorManager = errorManager
      confileHandler.formatter = ExtRecordFormatter(formatterPattern, formatterLogErrors)
      return ConsoleHandlerWrapper(confileHandler, filter)
    }
  }

  companion object {
    fun builder(): Builder {
      return Builder()
    }
  }
}
