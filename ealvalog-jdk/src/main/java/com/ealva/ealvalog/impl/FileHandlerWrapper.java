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

package com.ealva.ealvalog.impl;

import com.ealva.ealvalog.LoggerFilter;
import com.ealva.ealvalog.core.ExtRecordFormatter;
import com.ealva.ealvalog.filter.AlwaysNeutralFilter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;

/**
 * Wraps a FileHandler and provides a Builder
 *
 * Created by Eric A. Snell on 3/15/17.
 */
public class FileHandlerWrapper extends HandlerWrapper {
  @SuppressWarnings("WeakerAccess")
  public static Builder builder() {
    return new Builder();
  }

  private final FileHandler fileHandler;

  FileHandlerWrapper(@NotNull final FileHandler fileHandler, @NotNull final LoggerFilter filter) {
    super(fileHandler, filter);
    this.fileHandler = fileHandler;
  }

  private static Field fileNameField;

  public String getCurrentFileName() {
    if (fileHandler == null) {
      throw new IllegalArgumentException("fileHandler must not be null");
    }
    Field field = getFileNameField(fileHandler);
    if (field != null) {
      try {
        return (String)field.get(fileHandler);
      } catch (IllegalAccessException e) {
        System.err.println("Could not find field 'fileName' inside class " + fileHandler + " " + e);
      }
    }

    return null;
  }

  private synchronized Field getFileNameField(FileHandler fileHandler) {
    //TODO: this can potentially generate multiple errors for the same reason
    if (fileNameField == null) {
      try {
        //TODO: check if there is a better way
        fileNameField = fileHandler.getClass().getDeclaredField("fileName");
        fileNameField.setAccessible(true);
      } catch (NoSuchFieldException e) {
        System.err.println("Could not find field 'fileName' inside class " + fileHandler.getClass());
      }
    }
    return fileNameField;
  }

  /**
   * Build a {@link FileHandler} including an {@link ExtRecordFormatter}
   *
   * Created by Eric A. Snell on 3/8/17.
   */
  public static final class Builder {
    private String pattern;
    private int limit;
    private int count;
    private boolean append;
    private @NotNull String formatterPattern;
    private boolean formatterLogErrors;
    private @NotNull LoggerFilter filter;
    private @NotNull ErrorManager errorManager;

    Builder() {
      pattern = null;
      limit = 0;
      count = 1;
      append = false;
      formatterPattern = ExtRecordFormatter.TYPICAL_FORMAT;
      formatterLogErrors = true;
      filter = AlwaysNeutralFilter.INSTANCE;
      errorManager = new ErrorManager();
    }

    @SuppressWarnings("WeakerAccess")
    public Builder fileNamePattern(final @NotNull String pattern) {
      this.pattern = pattern;
      return this;
    }

    public Builder fileSizeLimit(final int limit) {
      this.limit = limit;
      return this;
    }

    public Builder maxFileCount(final int count) {
      this.count = count;
      return this;
    }

    public Builder appendToExisting(final boolean append) {
      this.append = append;
      return this;
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

    @SuppressWarnings("WeakerAccess")
    public FileHandlerWrapper build() throws IOException, IllegalStateException {
      if (pattern == null) {
        throw new IllegalStateException("File name pattern required");
      }
      final FileHandler fileHandler = new FileHandler(pattern, limit, count, append);
      fileHandler.setErrorManager(errorManager);
      fileHandler.setFormatter(new ExtRecordFormatter(formatterPattern, formatterLogErrors));
      return new FileHandlerWrapper(fileHandler, filter);
    }
  }
}
