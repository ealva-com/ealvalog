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

@file:Suppress("unused")

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
class FileHandlerWrapper(
  pattern: String = "ealvalog.%g.%u.log",
  limit: Int = 0,
  count: Int = 1,
  append: Boolean = false,
  formatterPattern: String = ExtRecordFormatter.TYPICAL_FORMAT,
  formatterLogErrors: Boolean = true,
  filter: LoggerFilter = AlwaysNeutralFilter,
  manager: ErrorManager = ErrorManager()
) : HandlerWrapper(
  FileHandler(pattern, limit, count, append).apply {
    errorManager = manager
    formatter = ExtRecordFormatter(formatterPattern, formatterLogErrors)
  },
  filter
) {

  val currentFileName: String?
    get() {
      val theField = getFileNameField(realHandler as FileHandler)
      return try {
        theField?.get(realHandler) as String
      } catch (e: IllegalAccessException) {
        System.err.println("Could not find field 'fileName' inside class $realHandler $e")
        null
      }
    }

  @Synchronized private fun getFileNameField(fileHandler: FileHandler): Field? {
    //TODO: this can potentially generate multiple errors for the same reason
    if (fileNameField == null) {
      try {
        //TODO: check if there is a better way
        fileNameField = fileHandler.javaClass.getDeclaredField("fileName")
        fileNameField?.isAccessible = true
      } catch (e: NoSuchFieldException) {
        System.err.println("Could not find field 'fileName' inside class " + fileHandler.javaClass)
      }

    }
    return fileNameField
  }

  /**
   * Build a [FileHandler] including an [ExtRecordFormatter]. For Java clients
   *
   * Created by Eric A. Snell on 3/8/17.
   */
  class Builder internal constructor(private val pattern: String) {
    private var limit: Int = 0
    private var count: Int = 1
    private var append: Boolean = false
    private var formatterPattern: String = ExtRecordFormatter.TYPICAL_FORMAT
    private var formatterLogErrors: Boolean = true
    private var filter: LoggerFilter = AlwaysNeutralFilter
    private var errorManager: ErrorManager = ErrorManager()

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
      return FileHandlerWrapper(
        pattern,
        limit,
        count,
        append,
        formatterPattern,
        formatterLogErrors,
        filter,
        errorManager
      )
    }
  }

  companion object {
    /** For Java clients */
    fun builder(fileNamePattern: String): Builder {
      return Builder(fileNamePattern)
    }

    private var fileNameField: Field? = null
  }
}
