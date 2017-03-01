package ealvalog;

import org.jetbrains.annotations.NotNull;

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
}
