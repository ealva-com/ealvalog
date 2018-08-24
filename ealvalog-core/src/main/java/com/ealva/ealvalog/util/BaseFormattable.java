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

package com.ealva.ealvalog.util;

import java.util.Formattable;

/**
 * Created by Eric A. Snell on 8/23/18.
 */
abstract class BaseFormattable implements Formattable {
  void maybePadAndJustify(final int width,
                          final boolean leftJustify,
                          final StringBuilder builder) {
    if (width != -1 && width > builder.length()) {
      builder.ensureCapacity(width);
      final int padAmount = width - builder.length();
      if (leftJustify) {
        for (int i = 0; i < padAmount; i++) {
          builder.append(' ');
        }
      } else {
        for (int i = 0; i < padAmount; i++) {
          builder.insert(0, ' ');
        }
      }
    }
  }
}
