package ealvalog.base;

import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Basic Marker implementation
 *
 * Created by Eric A. Snell on 2/28/17.
 */
public class MarkerImpl implements Marker {
  private final String name;
  private final List<Marker> children;

  public MarkerImpl(@NotNull final String name) {
    this.name = name;
    children = new CopyOnWriteArrayList<Marker>();
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
}
