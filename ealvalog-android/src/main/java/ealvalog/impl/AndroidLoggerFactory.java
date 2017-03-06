package ealvalog.impl;

import ealvalog.Logger;
import ealvalog.LoggerFactory;
import ealvalog.Marker;
import org.jetbrains.annotations.NotNull;

/**
 * Create AndroidLogger instances
 *
 * Created by Eric A. Snell on 3/4/17.
 */
public class AndroidLoggerFactory implements LoggerFactory {
  @NotNull @Override public Logger get(@NotNull final String name) {
    return new AndroidLogger(name);
  }

  @NotNull @Override public Logger get(@NotNull final String name, @NotNull final Marker marker) {
    return new AndroidLogger(name, marker);
  }
}
