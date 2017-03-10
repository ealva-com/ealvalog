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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Basic Marker implementation
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("WeakerAccess")
public class MarkerImpl implements Marker {
  private static final long serialVersionUID = 445780917635303838L;

  private static final char OPEN = '[';
  private static final char SEPARATOR = ',';
  private static final char CLOSE = ']';
  private String name;
  private List<Marker> children;

  public MarkerImpl(@NotNull final String name) {
    this.name = name;
    children = new CopyOnWriteArrayList<>();
  }

  @NotNull public String getName() {
    return name;
  }

  public boolean addChild(@NotNull final Marker child) {
    return children.add(child);
  }

  public boolean removeChild(@NotNull final Marker child) {
    return children.remove(child);
  }

  public boolean contains(@NotNull final Marker marker) {
    return this.equals(marker) || children.contains(marker);
  }

  public boolean contains(@NotNull final String markerName) {
    if (this.name.equals(markerName)) {
      return true;
    }
    for (Marker marker : children) {
      if (marker.contains(markerName)) {
        return true;
      }
    }
    return false;
  }

  public Iterator<Marker> iterator() {
    return children.iterator();
  }

  @Override public String toString() {
    if (children.isEmpty()) {
      return name;
    }
    return toStringBuilder(new StringBuilder()).toString();
  }

  @Override public @NotNull StringBuilder toStringBuilder(@NotNull final StringBuilder builder) {
    // subclasses would typically invoke super.toStringBuilder(builder) first
    if (children.isEmpty()) {
      return builder.append(name);
    }

    builder.append(name).append(OPEN);
    for (int i = 0, size = children.size(); i < size; i++) {
      if (i != 0) {
        builder.append(SEPARATOR);
      }
      children.get(i).toStringBuilder(builder);
    }
    builder.append(CLOSE);
    return builder;
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    name = in.readUTF();
    //noinspection unchecked
    children = (List<Marker>)in.readObject();
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeUTF(name);
    out.writeObject(children);
  }

}
