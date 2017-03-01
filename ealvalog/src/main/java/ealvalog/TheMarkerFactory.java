package ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * This is where {@link Marker} instances are obtained. This singleton must be configured with a concrete implementation of
 * {@link MarkerFactory} before use.
 * <p>
 * This class has a dependency on a concrete {@link MarkerFactory} instance which must be set before use. Setting and using this factory
 * is not thread safe - this is the responsibility of the client. It's expected this will be done during application load.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("unused")
public final class TheMarkerFactory {
  private static MarkerFactory markerFactory = NullMarkerFactory.INSTANCE;

  private TheMarkerFactory() {
  }

  public static Marker get(@NotNull final String name) {
    return markerFactory.get(name);
  }

  public static Marker orphan(@NotNull final String name) {
    return markerFactory.makeOrphan(name);
  }
}
