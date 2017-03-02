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
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A no-op implementation of the {@link Marker} interface
 *
 * Created by Eric A. Snell on 2/28/17.
 */
public enum NullMarker implements Marker {
  INSTANCE;

  @NotNull public String getName() {
    return "";
  }

  public boolean addChild(@NotNull final Marker child) {
    return false;
  }

  public boolean removeChild(@NotNull final Marker child) {
    return false;
  }

  public boolean contains(@NotNull final Marker marker) {
    return false;
  }

  public boolean contains(@NotNull final String markerName) {
    return false;
  }

  public Iterator<Marker> iterator() {
    return new Iterator<Marker>() {
      public void remove() {
      }

      public boolean hasNext() {
        return false;
      }

      public Marker next() {
        throw new NoSuchElementException();
      }
    };
  }

  public static Marker nullToNullInstance(final @Nullable Marker marker) {
    return marker == null ? NullMarker.INSTANCE : marker;
  }
}
