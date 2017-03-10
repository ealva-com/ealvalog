package ealvalog.impl;

import ealvalog.Logger;
import ealvalog.LoggerFilter;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Created by Eric A. Snell on 3/7/17.
 */
interface JdkLoggerConfiguration {
  void setLoggerFilter(@NotNull Logger logger, @NotNull LoggerFilter filter);

  void addLoggerHandler(@NotNull Logger logger, @NotNull LoggerHandler loggerHandler);

  JdkBridge getBridge(String loggerClassName);
}
