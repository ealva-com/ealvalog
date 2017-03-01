package ealvalog;

import org.jetbrains.annotations.NotNull;

/**
 * This is where {@link Logger} instances are obtained. This singleton must be configured with a concrete implementation of
 * {@link LoggerFactory} before use.
 * <p>
 * This class has a dependency on a concrete {@link LoggerFactory} instance which must be set before use. Setting and using this factory
 * is not thread safe - this is the responsibility of the client. It's expected this will be done during application load.
 * <p>
 * Created by Eric A. Snell on 2/28/17.
 */
@SuppressWarnings("unused")
public class TheLoggerFactory {
  private static LoggerFactory loggerFactory = NullLoggerFactory.INSTANCE;

  public static void setFactory(@NotNull final LoggerFactory factory) {
    loggerFactory = factory;
  }

  @NotNull
  public static Logger make(@NotNull final Class<?> aClass) {
    return loggerFactory.make(aClass.getName());
  }

  @NotNull
  public static Logger make(@NotNull final String name) {
    return loggerFactory.make(name);
  }


}
