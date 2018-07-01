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

package com.ealva.ealvalog.filter;

import com.ealva.ealvalog.FilterResult;
import com.ealva.ealvalog.LoggerFilter;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience base class for filters
 *
 * Created by Eric A. Snell on 3/13/17.
 */
public abstract class BaseFilter implements LoggerFilter {
  private @NotNull FilterResult whenMatched;
  private @NotNull FilterResult whenDiffer;

  @SuppressWarnings("WeakerAccess")
  protected BaseFilter() {
    whenMatched = FilterResult.NEUTRAL;
    whenDiffer = FilterResult.DENY;
  }

  @SuppressWarnings("WeakerAccess")
  protected BaseFilter(final @NotNull FilterResult whenMatched, final @NotNull FilterResult whenDiffer) {
    this.whenMatched = whenMatched;
    this.whenDiffer = whenDiffer;
  }

  @NotNull protected FilterResult getWhenMatched() {
    return whenMatched;
  }

  protected void setWhenMatched(@NotNull final FilterResult whenMatched) {
    this.whenMatched = whenMatched;
  }

  @NotNull protected FilterResult getWhenDiffer() {
    return whenDiffer;
  }

  protected void setWhenDiffer(@NotNull final FilterResult whenDiffer) {
    this.whenDiffer = whenDiffer;
  }

  @SuppressWarnings("WeakerAccess")
  protected FilterResult result(final boolean matched) {
    return matched ? whenMatched : whenDiffer;
  }
}
