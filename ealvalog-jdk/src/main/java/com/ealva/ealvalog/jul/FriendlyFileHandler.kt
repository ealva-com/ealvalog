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

package com.ealva.ealvalog.jul

import com.ealva.ealvalog.LoggerFilter
import com.ealva.ealvalog.core.ExtRecordFormatter
import com.ealva.ealvalog.filter.AlwaysNeutralFilter
import java.io.File
import java.io.IOException
import java.util.logging.ErrorManager
import java.util.logging.FileHandler
import java.util.regex.Pattern

@Suppress("unused")
/**
 * [Original on stackoverflow](https://stackoverflow.com/a/31536584/2660904)
 *
 * Created by Eric A. Snell on 8/18/18.
 */
class FriendlyFileHandler @Throws(IOException::class, SecurityException::class) constructor(
  private val fileNamePattern: String,
  byteLimitPerFile: Int,
  fileCount: Int,
  appendToExistingFile: Boolean = true,
  formatterPattern: String = ExtRecordFormatter.TYPICAL_FORMAT,
  formatterLogErrors: Boolean = true,
  loggerFilter: LoggerFilter = AlwaysNeutralFilter,
  manager: ErrorManager? = null
) :
  FileHandler(fileNamePattern, byteLimitPerFile, fileCount, appendToExistingFile) {

  init {
    formatter = ExtRecordFormatter(formatterPattern, formatterLogErrors)
    filter = loggerFilter
    manager?.let { mgr -> errorManager = mgr }
  }

  /***
   * Finds the most recent log file matching the pattern.
   * This is just a guess - if you have a complicated pattern
   * format it may not work.
   *
   * Currently supported format strings: g, u
   *
   * @return A File of the current log file, or null on error.
   */
  val currentLogFile: File?
    @Synchronized get() = synchronized(flushLock) {
      // so the file has the most recent date on it.
      flush()

      // handle incremental number formats and handle default case where %g is appended to end
      val patternRegex = "${fileNamePattern.replace("%[gu]".toRegex(), "\\\\d*")}(\\.\\d*)?$"

      val re = Pattern.compile(patternRegex)
      val matcher = re.matcher("")

      // check all files in the directory where this log would be
      val basedir = File(fileNamePattern).parentFile
      val logs = basedir.listFiles { pathname ->
        // only get files that are part of the pattern
        matcher.reset(pathname.absolutePath)
        matcher.find()
      }

      return findMostRecentLog(logs)
    }

  private fun findMostRecentLog(logs: Array<File>): File? {
    if (logs.isNotEmpty()) {
      var mostRecentDate: Long = 0
      var mostRecentIdx = 0

      for (i in logs.indices) {
        val d = logs[i].lastModified()
        if (d >= mostRecentDate) {
          mostRecentDate = d
          mostRecentIdx = i
        }
      }

      return logs[mostRecentIdx]
    } else {
      return null
    }
  }

  @Synchronized override fun flush() {
    // only let one Handler flush at a time.
    synchronized(flushLock) {
      super.flush()
    }
  }

  companion object {
    /***
     * In order to ensure the most recent log file is the file this one owns,
     * we flush before checking the directory for most recent file.
     *
     * But we must keep other log handlers from flushing in between and making
     * a NEW recent file.
     */
    private val flushLock = arrayOfNulls<Any>(0)
  }

}