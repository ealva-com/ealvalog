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

import android.app.Application
import com.ealva.ealvalog.LogLevel
import com.ealva.ealvalog.Loggers
import com.ealva.ealvalog.jdka.AndroidLoggerHandler
import com.ealva.ealvalog.jul.JdkLoggerFactory

/**
 * Created by Eric A. Snell on 8/15/18.
 */
class KotlinApp : Application() {
  override fun onCreate() {
    super.onCreate()
    // Configure the Loggers singleton
    val factory = JdkLoggerFactory
    Loggers.setFactory(factory)

    // Configure the underlying root java.util.logging.Logger
    val rootLogger = factory.root
    // rootLogger.setIncludeLocation(true); // unnecessary, can control at log site with + operator

    if (BuildConfig.DEBUG) {
      //      Fabric.with(this, CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
      rootLogger.addHandler(AndroidLoggerHandler.make())
      rootLogger.logLevel = LogLevel.WARN
    } else {
      //      Fabric.with(this, CrashlyticsCore(), Answers(), Crashlytics());
      //      rootLogger.addHandler(CrashlyticsLogHandler());
      //      rootLogger.logLevel(LogLevel.ERROR);
    }
  }
}