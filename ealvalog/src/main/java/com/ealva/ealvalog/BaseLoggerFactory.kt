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

abstract class BaseLoggerFactory : LoggerFactory {
    override fun get(name: String): Logger {
        return getLogger(name, null, false)
    }

    override fun get(name: String, includeLocation: Boolean): Logger {
        return getLogger(name, null, includeLocation)
    }

    override fun get(name: String, marker: Marker): Logger {
        return getLogger(name, marker, false)
    }

    override fun get(name: String, marker: Marker, includeLocation: Boolean): Logger {
        return getLogger(name, marker, includeLocation)
    }

    protected abstract fun getLogger(name: String, marker: Marker?, incLocation: Boolean): Logger
}
