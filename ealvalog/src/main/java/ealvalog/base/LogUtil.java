/*
 * Copyright 2017 Eric A. Snell
 *
 * This file is part of eAlvaLog.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ealvalog.base;

import org.jetbrains.annotations.NotNull;

/**
 * Various utility methods
 *
 * Created by Eric A. Snell on 3/1/17.
 */
@SuppressWarnings("WeakerAccess")
public final class LogUtil {
  /**
   * Make an Object[] from 2 original arrays. The first array was passed in by the client as original varargs. The second, our internal
   * varargs, should actually precede the original as they were listed first in the parameter list by the client.
   *
   * Note: the caller of this method could avoid an extra array creation. The caller knows the number of "extra" arguments and can avoid
   * the varargs array creation by "doing it yourself". Left as an exercise for the reader.
   *
   * @param formatArgs objects that come LAST in the resulting array
   * @param preceding  objects that come first, specified as varargs as a nicety
   *
   * @return joined array of objects, {@code preceding} first, followed by {@code formatArgs}
   */
  @NotNull public static Object[] combineArgs(@NotNull final Object[] formatArgs, @NotNull Object... preceding) {
    Object[] result = new Object[formatArgs.length + preceding.length];
    System.arraycopy(preceding, 0, result, 0, preceding.length);
    System.arraycopy(formatArgs, 0, result, preceding.length, formatArgs.length);
    return result;
  }

  /** Convenience for autoboxing primitives to an Object[] to pass to a {@link java.util.Formatter} */
  @NotNull public static Object[] convertToObjects(@NotNull Object... primitivesOrObjects) {
    return primitivesOrObjects;
  }

  private LogUtil() {
  }

  public static StackTraceElement getCallerLocation(final int currentStackDepthFromCallSite) {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    if (stackTrace.length <= currentStackDepthFromCallSite + 1) {
      throw new IllegalStateException("Not enough stack trace elements for given call depth. Possible optimizer/obfuscator?");
    }
    return stackTrace[currentStackDepthFromCallSite + 1];
  }
}
