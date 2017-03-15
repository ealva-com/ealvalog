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

package ealvalog.impl;

import ealvalog.LoggerFilter;
import ealvalog.filter.AlwaysNeutralFilter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

/**
 * Wraps a ConsoleHandler and provides a builder
 *
 * Created by Eric A. Snell on 3/15/17.
 */
public class ConsoleHandlerWrapper extends HandlerWrapper {
  public static Builder builder() {
    return new Builder();
  }

  ConsoleHandlerWrapper(@NotNull final Handler realHandler, @NotNull final LoggerFilter filter) {
    super(realHandler, filter);
  }

  /**
   * Build a {@link FileHandler} including an {@link ExtRecordFormatter}
   *
   * Created by Eric A. Snell on 3/8/17.
   */
  public static final class Builder {
    private @NotNull String formatterPattern;
    private boolean formatterLogErrors;
    private @NotNull LoggerFilter filter;
    private @NotNull ErrorManager errorManager;

    Builder() {
      formatterPattern = ExtRecordFormatter.TYPICAL_FORMAT;
      formatterLogErrors = true;
      filter = AlwaysNeutralFilter.INSTANCE;
      errorManager = new ErrorManager();
    }

    public Builder extRecordFormatterPattern(final @NotNull String pattern) {
      this.formatterPattern = pattern;
      return this;
    }

    public Builder formatterLogErrors(final boolean logErrors) {
      this.formatterLogErrors = logErrors;
      return this;
    }

    public Builder filter(final @NotNull LoggerFilter filter) {
      this.filter = filter;
      return this;
    }

    public Builder errorManager(final @NotNull ErrorManager errorManager) {
      this.errorManager = errorManager;
      return this;
    }

    public ConsoleHandlerWrapper build() throws IOException, IllegalStateException {
      final ConsoleHandler confileHandler = new ConsoleHandler();
      confileHandler.setErrorManager(errorManager);
      confileHandler.setFormatter(new ExtRecordFormatter(formatterPattern, formatterLogErrors));
      return new ConsoleHandlerWrapper(confileHandler, filter);
    }
  }
}
