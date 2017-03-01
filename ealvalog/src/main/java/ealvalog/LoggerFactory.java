package ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * Creates {@link Logger} instances, typically based on the class which will be logging.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
public interface LoggerFactory {
  /**
   * Make a Logger instance with the given name
   *
   * @param name the name of the logger. This name will be treated as if it were a class name with the canonical package hierarchy
   *
   * @return {@link Logger} instance
   */
  @NotNull Logger make(@NotNull String name);
}
