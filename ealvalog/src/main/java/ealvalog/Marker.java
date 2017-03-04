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

package ealvalog;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Iterator;

/**
 * A Marker is extra data passed to the underlying logging system and it's up to that implementation on if/how a Marker is used. Examples
 * might be that the Marker is output along with the log message or the Marker might be used to route the log message.
 *
 * Created by Eric A. Snell on 2/28/17.
 */
public interface Marker extends Serializable, Iterable<Marker> {
  @NotNull String getName();

  boolean addChild(@NotNull Marker child);

  boolean removeChild(@NotNull Marker child);

  /**
   * @return true if this instance is {@code marker} or this instance has {@code marker} as a child
   */
  boolean contains(@NotNull Marker marker);

  /**
   * @return true if this instance is named {@code markerName} or this instance has a child named {@code markName}
   */
  boolean contains(@NotNull String markerName);

  /**
   * @return an iterator over child markers
   */
  Iterator<Marker> iterator();
}
