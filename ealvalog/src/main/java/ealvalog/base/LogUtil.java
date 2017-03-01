package ealvalog.base;

/**
 * Various utility methods
 *
 * Created by Eric A. Snell on 3/1/17.
 */
public final class LogUtil {
  private LogUtil() {
  }

  public static StackTraceElement getCallerLocation(final int stackDepthFromCallSite) {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    if (stackTrace.length <= stackDepthFromCallSite) {
      throw new IllegalStateException("Not enough stack trace elements for given call depth. Possible optimizer/obfuscator?");
    }
    return stackTrace[stackDepthFromCallSite];
  }
}
