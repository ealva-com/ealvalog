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

package com.ealva.ealvalog.java

import com.ealva.ealvalog.LoggerFactory
import com.ealva.ealvalog.Marker

/**
 * Created by Eric A. Snell on 8/10/18.
 */
interface JLoggerFactory <T : JLogger> : LoggerFactory {
  override fun get(name: String, marker: Marker?, includeLocation: Boolean): T
}