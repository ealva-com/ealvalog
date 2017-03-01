package ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * A no-op {@link LoggerFactory} instance - returns only {@link NullLogger#INSTANCE}
 *
 * Created by Eric A. Snell on 2/28/17.
 */
public enum NullLoggerFactory implements LoggerFactory {
  INSTANCE;

  @NotNull public Logger make(@NotNull final String name) {
    return NullLogger.INSTANCE;
  }
}
