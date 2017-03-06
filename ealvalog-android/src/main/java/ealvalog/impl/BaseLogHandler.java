package ealvalog.impl;

import ealvalog.Marker;
import ealvalog.util.LogMessageFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import android.os.Build;
import android.util.Log;

import java.util.Locale;

/**
 * Base handler prepares a default style message including log call site information
 *
 * Created by Eric A. Snell on 3/3/17.
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseLogHandler implements LogHandler {
  protected Locale getLocale() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      return Locale.getDefault(Locale.Category.FORMAT);
    } else {
      return Locale.getDefault();
    }
  }

  @Override
  public void prepareLog(final String tag,
                         final int level,
                         @Nullable final Marker marker,
                         @Nullable final Throwable throwable,
                         @Nullable final StackTraceElement callerLocation,
                         @NotNull final LogMessageFormatter formatter,
                         @NotNull final String msg,
                         @NotNull final Object... formatArgs) {
    if (callerLocation != null) {
      formatter.getBuilder().append(callerLocation.getMethodName()).append(':').append(callerLocation.getLineNumber()).append(' ');
    }
    final String logMessage = formatter.format(getLocale(), msg, formatArgs)
                                       .toString();
    log(level, tag, logMessage, throwable);
  }

  protected void log(final int level,
                     final String tag,
                     final String logMessage,
                     final @Nullable Throwable throwable) {
    switch (level) {
      case Log.VERBOSE:
        Log.v(tag, logMessage, throwable);
        break;
      case Log.DEBUG:
        Log.d(tag, logMessage, throwable);
        break;
      case Log.INFO:
        Log.i(tag, logMessage, throwable);
        break;
      case Log.WARN:
        Log.w(tag, logMessage, throwable);
        break;
      case Log.ERROR:
        Log.e(tag, logMessage, throwable);
        break;
      case Log.ASSERT:
        Log.wtf(tag, logMessage, throwable);
        break;
      default:
        Log.wtf(tag, "LOGGER CONFIG ERROR (" + level + ") " + logMessage, throwable);
        break;
    }
  }

}
