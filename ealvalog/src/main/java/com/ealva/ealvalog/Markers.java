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

import org.jetbrains.annotations.NotNull;

/**
 * This is where {@link com.ealva.ealvalog.Marker} instances are obtained. This singleton must be configured with a concrete implementation of
 * {@link com.ealva.ealvalog.MarkerFactory} before use.
 * <p>
 * This class has a dependency on a concrete {@link com.ealva.ealvalog.MarkerFactory} instance which must be set before use. Setting and using this factory
 * is not thread safe - this is the responsibility of the client. It's expected this will be done during application load.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("unused")
public final class Markers {
  private static com.ealva.ealvalog.MarkerFactory
      markerFactory = com.ealva.ealvalog.NullMarkerFactory.INSTANCE;

  private Markers() {
  }

  public static com.ealva.ealvalog.Marker get(@NotNull final String name) {
    return markerFactory.get(name);
  }

  public static com.ealva.ealvalog.Marker orphan(@NotNull final String name) {
    return markerFactory.makeOrphan(name);
  }
}
