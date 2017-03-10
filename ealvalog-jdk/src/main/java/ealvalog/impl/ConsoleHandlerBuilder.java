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

import ealvalog.AlwaysYesFilter;
import ealvalog.LoggerFilter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;

/**
 * Build a {@link FileHandler} including an {@link ExtRecordFormatter}
 *
 * Created by Eric A. Snell on 3/8/17.
 */
public final class ConsoleHandlerBuilder {
  private @NotNull String formatterPattern;
  private boolean formatterLogErrors;
  private @NotNull LoggerFilter filter;
  private @NotNull ErrorManager errorManager;

  ConsoleHandlerBuilder() {
    formatterPattern = ExtRecordFormatter.TYPICAL_FORMAT;
    formatterLogErrors = true;
    filter = AlwaysYesFilter.INSTANCE;
    errorManager = new ErrorManager();
  }

  public ConsoleHandlerBuilder extRecordFormatterPattern(final @NotNull String pattern) {
    this.formatterPattern = pattern;
    return this;
  }

  public ConsoleHandlerBuilder formatterLogErrors(final boolean logErrors) {
    this.formatterLogErrors = logErrors;
    return this;
  }

  public ConsoleHandlerBuilder filter(final @NotNull LoggerFilter filter) {
    this.filter = filter;
    return this;
  }

  public ConsoleHandlerBuilder errorManager(final @NotNull ErrorManager errorManager) {
    this.errorManager = errorManager;
    return this;
  }

  public LoggerHandler build() throws IOException, IllegalStateException {
    final ConsoleHandler confileHandler = new ConsoleHandler();
    confileHandler.setErrorManager(errorManager);
    confileHandler.setFormatter(new ExtRecordFormatter(formatterPattern, formatterLogErrors));
    return new LoggerHandler(confileHandler, filter);
  }
}
