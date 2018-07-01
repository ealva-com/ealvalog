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

package com.ealva.ealvalog.core;

import com.ealva.ealvalog.Marker;
import com.ealva.ealvalog.MarkerFactory;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Factory which creates {@link Marker} instances
 *
 * Created by Eric A. Snell on 2/28/17.
 */
public class MarkerFactoryImpl implements MarkerFactory {
  private final ConcurrentMap<String, Marker> nameMarkerMap;

  public MarkerFactoryImpl() {
    this.nameMarkerMap = new ConcurrentHashMap<>();
  }

  @NotNull public Marker get(@NotNull final String name) {
    Marker marker = nameMarkerMap.get(name);
    if (marker == null) {
      marker = new com.ealva.ealvalog.core.MarkerImpl(name, this);
      Marker oldMarker = nameMarkerMap.putIfAbsent(name, marker);
      if (oldMarker != null) {
        marker = oldMarker;
      }
    }
    return marker;
  }

  public boolean exists(@NotNull final String name) {
    return nameMarkerMap.containsKey(name);
  }

  public boolean orphan(@NotNull final String name) {
    return nameMarkerMap.remove(name) != null;
  }

  @NotNull public Marker makeOrphan(@NotNull final String name) {
    return new com.ealva.ealvalog.core.MarkerImpl(name, this);
  }
}
