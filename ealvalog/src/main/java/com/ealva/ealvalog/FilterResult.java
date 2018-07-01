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

package com.ealva.ealvalog;

/**
 * Filter results are used to determine if logging passes various thresholds. Because filtering may include several factors, some filters
 * may respond with NONE to allow processing to continue to further checks. An example would be a composite filter where contained filters
 * need to allow processing to move down the chain rather than ACCEPT or DENY.
 * <p>
 * Created by Eric A. Snell on 3/13/17.
 */
public enum FilterResult {
  ACCEPT,
  DENY,
  NEUTRAL

}
