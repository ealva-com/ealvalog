/*
 * Copyright 2017 Eric A. Snell
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

/**
 * Various utility methods
 *
 * Created by Eric A. Snell on 3/1/17.
 */
@SuppressWarnings("WeakerAccess")
public final class LogUtil {
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
