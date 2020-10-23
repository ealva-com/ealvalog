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

package com.ealva.kotlinapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ealva.ealvalog.e
import com.ealva.ealvalog.i
import com.ealva.ealvalog.invoke
import com.ealva.ealvalog.logger
import com.ealva.ealvalog.unaryPlus
import com.ealva.ealvalog.w
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val LOG = logger(MainActivity::class)

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    LOG.e { it("onCreate %1\$tH:%1\$tM:%1\$tS.%1\$tL", System.currentTimeMillis()) }
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    GlobalScope.launch {
      something()
    }
  }

  override fun onStart() {
    super.onStart()
    OTHER_LOG.w { it("onStart 0x%s 0x%X", Integer.toHexString(256), 256) }

    // Should not log because root logger log level was set to LogLevel.WARN in JavaApp
    LOG.i { it("SHOULD NOT BE LOGGED") }
  }

  override fun onResume() {
    super.onResume()
    // includes location information with + operator. Location information is correct since the
    // lambda is inlined
    LOG.w { +it("onResume %.6f", Math.PI) }
    try {
      throwsSomething()
    } catch (ex: RuntimeException) {
      LOG.e(ex) { +it("Caught: %s", ex.message ?: "") }  // log exception with location information
    }
  }

  private fun something() {
    LOG.e { +it("test") }
  }

  private fun throwsSomething() {
    val ex = RuntimeException("Exception Message")
    LOG.e(ex) { +it("Throwing: %s", ex.message ?: "") }
    throw ex
  }

  companion object {
    private val OTHER_LOG = logger()
  }

}
