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
