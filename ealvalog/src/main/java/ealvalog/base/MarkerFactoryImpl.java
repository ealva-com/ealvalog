package ealvalog.base;

import ealvalog.Marker;
import ealvalog.MarkerFactory;
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
    this.nameMarkerMap = new ConcurrentHashMap<String, Marker>();
  }

  @NotNull public Marker get(@NotNull final String name) {
    Marker marker = nameMarkerMap.get(name);
    if (marker == null) {
      marker = new MarkerImpl(name);
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
    return new MarkerImpl(name);
  }
}
