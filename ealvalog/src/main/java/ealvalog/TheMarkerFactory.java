/*
 * Copyright 2017 Eric A. Snell
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

package ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * This is where {@link Marker} instances are obtained. This singleton must be configured with a concrete implementation of
 * {@link MarkerFactory} before use.
 * <p>
 * This class has a dependency on a concrete {@link MarkerFactory} instance which must be set before use. Setting and using this factory
 * is not thread safe - this is the responsibility of the client. It's expected this will be done during application load.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("unused")
public final class TheMarkerFactory {
  private static MarkerFactory markerFactory = NullMarkerFactory.INSTANCE;

  private TheMarkerFactory() {
  }

  public static Marker get(@NotNull final String name) {
    return markerFactory.get(name);
  }

  public static Marker orphan(@NotNull final String name) {
    return markerFactory.makeOrphan(name);
  }
}
