package ealvalog.util;

import org.jetbrains.annotations.Nullable;

/**
 * A no-op throwable that is only usable as a placeholder for an absent {@code Optional<Throwable>}
 *
 * Created by Eric A. Snell on 3/5/17.
 */
public final class NullThrowable extends Throwable {
  public static final Throwable INSTANCE = new NullThrowable();

  public static Throwable nullToNullInstance(@Nullable final Throwable throwable) {
    return throwable == null ? INSTANCE : throwable;
  }

  private NullThrowable() {
    super("");
  }

  @Override public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
