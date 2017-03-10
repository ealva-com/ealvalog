package ealvalog.util;

import ealvalog.base.LogUtil;
import org.jetbrains.annotations.Nullable;

import static java.util.FormattableFlags.ALTERNATE;

import java.util.Formattable;
import java.util.FormattableFlags;
import java.util.Formatter;

/**
 * Contains an optional {@link Throwable} for formatting on log record output. Will format to {@link Throwable#toString()} except if the
 * {@link FormattableFlags#ALTERNATE} is specified ($#), then the stack trace is included.
 *
 * Created by Eric A. Snell on 3/8/17.
 */
@SuppressWarnings("unused")
public class FormattableThrowable extends Throwable implements Formattable {

  private @Nullable Throwable realThrowable;

  public FormattableThrowable(final @Nullable Throwable throwable) {
    realThrowable = throwable;
  }

  @Override public void formatTo(final Formatter formatter, final int flags, final int width, final int precision) {
    if (realThrowable != null) {
      if ((flags & ALTERNATE) == ALTERNATE) {
        formatter.format(LogUtil.getStackTraceAsString(realThrowable));
      } else {
        formatter.format(realThrowable.toString());
      }
    } else {
      formatter.format("");
    }
  }

  @Override public String getMessage() {
    return realThrowable != null ? realThrowable.getMessage() : "";
  }

  @Override public String toString() {
    return realThrowable != null ? realThrowable.toString() : "";
  }

  public @Nullable Throwable getRealThrowable() {
    return realThrowable;
  }

  public FormattableThrowable setRealThrowable(final @Nullable Throwable realThrowable) {
    this.realThrowable = realThrowable;
    return this;
  }
}
