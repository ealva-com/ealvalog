package ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * A no-op implemntation of the {@link MarkerFactory} interface
 *
 * Created by Eric A. Snell on 2/28/17.
 */
public enum NullMarkerFactory implements MarkerFactory {
  INSTANCE;

  @NotNull public Marker get(@NotNull final String name) {
    return NullMarker.INSTANCE;
  }

  public boolean exists(@NotNull final String name) {
    return false;
  }

  @Override public boolean orphan(@NotNull final String name) {
    return false;
  }

  @NotNull public Marker makeOrphan(@NotNull final String name) {
    return NullMarker.INSTANCE;
  }
}
