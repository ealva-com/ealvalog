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

package ealvalog.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Various utility methods
 * <p>
 * Created by Eric A. Snell on 3/1/17.
 */
public final class LogUtil {
  private LogUtil() {
  }

  /**
   * Make an Object[] from 2 original arrays. The first array was passed in by the client as original varargs. The second, our internal
   * varargs, should actually precede the original as they were listed first in the parameter list by the client.
   *
   * @param formatArgs objects that come LAST in the resulting array
   * @param preceding  objects that come first, specified as varargs as a nicety
   *
   * @return joined array of objects, {@code preceding} first, followed by {@code formatArgs}
   */
  public static @NotNull Object[] combineArgs(@NotNull final Object[] formatArgs, @NotNull Object... preceding) {
    return combineArgs(new Object[formatArgs.length + preceding.length], formatArgs, preceding);
  }

  /**
   * Combine 2 Object[]. The first array was passed in by the client as original varargs. The second, our internal
   * varargs, should actually precede the original as they were listed first in the parameter list by the client.
   *
   * @param destination both {@code formatArgs} and {@code preceding} are copied into this array and returned, if it is large enough.
   *                    Otherwise a new Object[] is created, copied into, and returned. Array indices past what is required are not
   *                    modified.
   * @param formatArgs  objects that come LAST in the resulting array
   * @param preceding   objects that come first, specified as varargs as a nicety
   *
   * @return joined array of objects, {@code preceding} first, followed by {@code formatArgs}
   */
  public static @NotNull Object[] combineArgs(final @NotNull Object[] destination,
                                              @NotNull final Object[] formatArgs,
                                              @NotNull Object... preceding) {
    final int requiredLength = formatArgs.length + preceding.length;
    Object[] result = destination.length >= requiredLength ? destination : new Object[requiredLength];
    System.arraycopy(preceding, 0, result, 0, preceding.length);
    System.arraycopy(formatArgs, 0, result, preceding.length, formatArgs.length);
    return result;
  }

  /**
   * Convenience for autoboxing primitives to an Object[] to pass to a {@link java.util.Formatter}
   *
   * @param primitivesOrObjects list to convert to array
   *
   * @return array for all the primitives or objects pass as parameters
   */
  @SuppressWarnings("unused")
  public static @NotNull Object[] convertToObjects(@NotNull Object... primitivesOrObjects) {
    return primitivesOrObjects;
  }

  /**
   * Get a {@link StackTraceElement} at an index offset from the current stack position.
   *
   * @param currentStackDepthFromCallSite the index of the stack depth to return
   *
   * @return a {@link StackTraceElement} from the requested stack index
   *
   * @throws IllegalStateException if the call stack is to small for the depth requested. Typically this would only happen if the calling
   *                               code has a defect and is incorrectly indexing into the stack or if some type of obfuscation/shrinker tool
   *                               modified the code in some way (eg. Proguard)
   */
  public static StackTraceElement getCallerLocation(final int currentStackDepthFromCallSite) {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    if (stackTrace.length <= currentStackDepthFromCallSite + 1) {
      throw new IllegalStateException("Not enough stack trace elements for given call depth. Possible optimizer/obfuscator?");
    }
    return stackTrace[currentStackDepthFromCallSite + 1];
  }

  /**
   * Early Android doesn't provide Collections.emptyIterator(), so we will.
   * @param <T> type to iterate over
   * @return an Iterator over type T which is empty
   */
  public static <T> Iterator<T> emptyIterator() {
    //noinspection unchecked
    return (Iterator<T>)EmptyIterator.EMPTY_ITERATOR;
  }

}
