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
import java.lang.reflect.Field
import java.util.logging.ErrorManager
import java.util.logging.FileHandler

/**
 * Wraps a FileHandler and provides a Builder
 *
 * Created by Eric A. Snell on 3/15/17.
 */
class FileHandlerWrapper internal constructor(
  private val fileHandler: FileHandler,
  filter: LoggerFilter
) : HandlerWrapper(fileHandler, filter) {

  val currentFileName: String?
    get() {
      val theField = getFileNameField(fileHandler)
      if (theField != null) {
        try {
          return theField.get(fileHandler) as String
        } catch (e: IllegalAccessException) {
          System.err.println("Could not find field 'fileName' inside class $fileHandler $e")
        }

      }

      return null
    }

  @Synchronized private fun getFileNameField(fileHandler: FileHandler): Field? {
    //TODO: this can potentially generate multiple errors for the same reason
    if (fileNameField == null) {
      try {
        //TODO: check if there is a better way
        fileNameField = fileHandler.javaClass.getDeclaredField("fileName")
        fileNameField!!.isAccessible = true
      } catch (e: NoSuchFieldException) {
        System.err.println("Could not find field 'fileName' inside class " + fileHandler.javaClass)
      }

    }
    return fileNameField
  }

  /**
   * Build a [FileHandler] including an [ExtRecordFormatter]
   *
   * Created by Eric A. Snell on 3/8/17.
   */
  class Builder internal constructor() {
    private var pattern: String? = null
    private var limit: Int = 0
    private var count: Int = 0
    private var append: Boolean = false
    private var formatterPattern: String
    private var formatterLogErrors: Boolean = false
    private var filter: LoggerFilter
    private var errorManager: ErrorManager

    init {
      pattern = null
      limit = 0
      count = 1
      append = false
      formatterPattern = ExtRecordFormatter.TYPICAL_FORMAT
      formatterLogErrors = true
      filter = AlwaysNeutralFilter
      errorManager = ErrorManager()
    }

    fun fileNamePattern(pattern: String): Builder {
      this.pattern = pattern
      return this
    }

    fun fileSizeLimit(limit: Int): Builder {
      this.limit = limit
      return this
    }

    fun maxFileCount(count: Int): Builder {
      this.count = count
      return this
    }

    fun appendToExisting(append: Boolean): Builder {
      this.append = append
      return this
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
    fun build(): FileHandlerWrapper {
      if (pattern == null) {
        throw IllegalStateException("File name pattern required")
      }
      val fileHandler = FileHandler(pattern!!, limit, count, append)
      fileHandler.errorManager = errorManager
      fileHandler.formatter = ExtRecordFormatter(formatterPattern, formatterLogErrors)
      return FileHandlerWrapper(fileHandler, filter)
    }
  }

  companion object {
    fun builder(): Builder {
      return Builder()
    }

    private var fileNameField: Field? = null
  }
}
