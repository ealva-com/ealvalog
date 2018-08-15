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
import com.ealva.ealvalog.java.JLogger;
import com.ealva.ealvalog.java.JLoggers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  // We're using JLoggers/JLogger for the Java logging interface
  private static final JLogger LOG = JLoggers.get(MainActivity.class);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    LOG.log(LogLevel.ERROR, "onCreate %1$tH:%1$tM:%1$tS.%1$tL", System.currentTimeMillis());
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override protected void onStart() {
    super.onStart();
    LOG.log(LogLevel.WARN, "onStart 0x%s 0x%X", Integer.toHexString(256), 256);

    // Should not log because root logger log level was set to LogLevel.WARN in JavaApp
    LOG.log(LogLevel.INFO, "SHOULD NOT BE LOGGED");
  }

  @Override protected void onResume() {
    super.onResume();
    LOG.log(LogLevel.WARN, "onResume %.6f", Math.PI);
  }
}
