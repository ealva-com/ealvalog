package ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * Creates and maintains {@link Marker} instances
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public interface MarkerFactory {

  /**
   * Get a Marker instance, creating it if necessary.
   *
   * @param name name of the marker
   *
   * @return a marker instance contained in this factory
   */
  @NotNull Marker get(@NotNull String name);

  /**
   * Determine if the Marker has already been created
   *
   * @param name name of the marker
   *
   * @return true if a marker with {@code name} exists in this factory instance
   */
  boolean exists(@NotNull String name);

  /**
   * Remove the named {@link Marker} from the factory
   * @param name marker name to remove or create
   *
   * @return true if the {@link Marker} was removed, false if it was not contained in this factory
   */
  boolean orphan(@NotNull String name);

  /**
   * Create a {@link Marker} instance but do not keep a reference in this factory
   *
   * @param name name of the marker to be created
   *
   * @return a new marker instance that is not retained by this factory
   */
  @NotNull Marker makeOrphan(@NotNull String name);
}
