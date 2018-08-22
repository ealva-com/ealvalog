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

package com.ealva.javaapp;

import com.ealva.ealvalog.LogLevel;
import com.ealva.ealvalog.Loggers;
import com.ealva.ealvalog.jdka.AndroidLoggerHandler;
import com.ealva.ealvalog.jul.JdkLogger;
import com.ealva.ealvalog.jul.JdkLoggerFactory;

import android.app.Application;

/**
 * Created by Eric A. Snell on 8/14/18.
 */
public class JavaApp extends Application {
  @Override public void onCreate() {
    super.onCreate();

    // Configure the Loggers singleton
    JdkLoggerFactory factory = JdkLoggerFactory.INSTANCE;
    Loggers.INSTANCE.setFactory(factory);

    // Configure the underlying root java.util.logging.Logger
    JdkLogger rootLogger = JdkLoggerFactory.INSTANCE.getRoot();
    rootLogger.setIncludeLocation(true); // makes logging more expensive

    if (BuildConfig.DEBUG) {
//      Fabric.with(this, CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
      rootLogger.addHandler(AndroidLoggerHandler.Companion.make());
      rootLogger.setLogLevel(LogLevel.WARN);
    } else {
//      Fabric.with(this, CrashlyticsCore(), Answers(), Crashlytics());
//      rootLogger.addHandler(CrashlyticsLogHandler());
//      rootLogger.logLevel(LogLevel.ERROR);
    }
  }
}
