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
import org.jetbrains.annotations.Nullable;

import java.util.Formatter;
import java.util.Iterator;

/**
 * A no-op implementation of the {@link Marker} interface
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public enum NullMarker implements Marker {
  INSTANCE;

  @NotNull public String getName() {
    return "";
  }

  public boolean add(@NotNull final Marker marker) {
    return false;
  }

  public boolean remove(@NotNull final Marker marker) {
    return false;
  }

  public boolean isOrContains(@NotNull final Marker marker) {
    return false;
  }

  public boolean isOrContains(@NotNull final String markerName) {
    return false;
  }

  public Iterator<Marker> iterator() {
    return com.ealva.ealvalog.util.LogUtil.emptyIterator();
  }

  @NotNull @Override public StringBuilder toStringBuilder(@NotNull final StringBuilder builder, final boolean includeContained) {
    return builder;
  }

  public static Marker nullToNullInstance(final @Nullable Marker marker) {
    return marker == null ? NullMarker.INSTANCE : marker;
  }

  @Override public void formatTo(final Formatter formatter, final int flags, final int width, final int precision) {}
}
