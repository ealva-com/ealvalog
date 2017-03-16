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

package ealvalog.core;

import ealvalog.Marker;
import ealvalog.MarkerFactory;
import org.jetbrains.annotations.NotNull;

import static java.util.FormattableFlags.ALTERNATE;
import static java.util.FormattableFlags.LEFT_JUSTIFY;
import static java.util.FormattableFlags.UPPERCASE;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Basic Marker implementation
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public class MarkerImpl implements Marker {
  private static final long serialVersionUID = 445780917635303838L;

  private static final char OPEN = '[';
  private static final char SEPARATOR = ',';
  private static final char CLOSE = ']';
  private @NotNull String name;
  private @NotNull final MarkerFactory markerFactory;
  private @NotNull List<Marker> containedMarkers;

  public MarkerImpl(final @NotNull String name, final @NotNull MarkerFactory markerFactory) {
    this.name = name;
    this.markerFactory = markerFactory;
    containedMarkers = new CopyOnWriteArrayList<>();
  }

  @NotNull public String getName() {
    return name;
  }

  public boolean add(@NotNull final Marker marker) {
    return containedMarkers.add(marker);
  }

  public boolean remove(@NotNull final Marker marker) {
    return containedMarkers.remove(marker);
  }

  public boolean isOrContains(@NotNull final Marker marker) {
    return this.equals(marker) || containedMarkers.contains(marker);
  }

  public boolean isOrContains(@NotNull final String markerName) {
    return isOrContains(markerFactory.get(markerName));
  }

  public Iterator<Marker> iterator() {
    return containedMarkers.iterator();
  }

  @Override public String toString() {
    if (containedMarkers.isEmpty()) {
      return name;
    }
    return toStringBuilder(new StringBuilder(), true).toString();
  }

  @Override public @NotNull StringBuilder toStringBuilder(@NotNull final StringBuilder builder, final boolean includeContained) {
    // subclasses would typically invoke super.toStringBuilder(builder) first
    if (containedMarkers.isEmpty()) {
      return builder.append(name);
    }

    if (includeContained) {
      builder.append(name).append(OPEN);
      for (int i = 0, size = containedMarkers.size(); i < size; i++) {
        if (i != 0) {
          builder.append(SEPARATOR);
        }
        containedMarkers.get(i).toStringBuilder(builder, true);
      }
      builder.append(CLOSE);
    }

    return builder;
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    name = in.readUTF();
    //noinspection unchecked
    containedMarkers = (List<Marker>)in.readObject();
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeUTF(name);
    out.writeObject(containedMarkers);
  }

  /**
   * @param o other marker
   * @return true if the same instance or the names are equal
   */
  @Override public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }

    final MarkerImpl markers = (MarkerImpl)o;

    return name.equals(markers.name);
  }

  @Override public int hashCode() {
    return name.hashCode();
  }

  @Override public void formatTo(final Formatter formatter, final int flags, final int width, final int precision) {
    final boolean useAlternate = (flags & ALTERNATE) == ALTERNATE;
    final boolean leftJustify = (flags & LEFT_JUSTIFY) == LEFT_JUSTIFY;
    final boolean upperCase = (flags & UPPERCASE) == UPPERCASE;
    final StringBuilder builder = getStringBuilder();
    toStringBuilder(builder, useAlternate);
    if (precision != -1 && builder.length() > precision) {
      builder.setLength(precision - 1);
      builder.append('…');
    }
    if (width != -1 && width > builder.length()) {
      builder.ensureCapacity(width);
      final int padAmount = width - builder.length();
      if (leftJustify) {
        for (int i = 0; i < padAmount; i++) {
          builder.append(' ');
        }
      } else {
        for (int i = 0; i < padAmount; i++) {
          builder.insert(0, ' ');  // could possibly be more efficient to insert from long string of spaces, but Knuth would not approve
        }
      }
    }
    formatter.format(upperCase ? builder.toString().toUpperCase() : builder.toString());
  }

  private StringBuilder getStringBuilder() {
    return new StringBuilder(); // threadLocalStringBuilder.get(); Not thread local builder until we know we're logging markers a lot
  }
}
