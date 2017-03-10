package ealvalog.impl;

import ealvalog.AlwaysYesFilter;
import ealvalog.LoggerFilter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.ErrorManager;
import java.util.logging.FileHandler;

/**
 * Build a {@link FileHandler} including an {@link ExtRecordFormatter}
 *
 * Created by Eric A. Snell on 3/8/17.
 */
public final class FileHandlerBuilder {
  private String pattern;
  private int limit;
  private int count;
  private boolean append;
  private @NotNull String formatterPattern;
  private boolean formatterLogErrors;
  private @NotNull LoggerFilter filter;
  private @NotNull ErrorManager errorManager;

  FileHandlerBuilder() {
    pattern = null;
    limit = 0;
    count = 1;
    append = false;
    formatterPattern = ExtRecordFormatter.TYPICAL_FORMAT;
    formatterLogErrors = true;
    filter = AlwaysYesFilter.INSTANCE;
    errorManager = new ErrorManager();
  }

  @SuppressWarnings("WeakerAccess")
  public FileHandlerBuilder fileNamePattern(final @NotNull String pattern) {
    this.pattern = pattern;
    return this;
  }

  public FileHandlerBuilder fileSizeLimit(final int limit) {
    this.limit = limit;
    return this;
  }

  public FileHandlerBuilder maxFileCount(final int count) {
    this.count = count;
    return this;
  }

  public FileHandlerBuilder appendToExisting(final boolean append) {
    this.append = append;
    return this;
  }

  public FileHandlerBuilder extRecordFormatterPattern(final @NotNull String pattern) {
    this.formatterPattern = pattern;
    return this;
  }

  public FileHandlerBuilder formatterLogErrors(final boolean logErrors) {
    this.formatterLogErrors = logErrors;
    return this;
  }

  public FileHandlerBuilder filter(final @NotNull LoggerFilter filter) {
    this.filter = filter;
    return this;
  }

  public FileHandlerBuilder errorManager(final @NotNull ErrorManager errorManager) {
    this.errorManager = errorManager;
    return this;
  }

  @SuppressWarnings("WeakerAccess")
  public LoggerHandler build() throws IOException, IllegalStateException {
    if (pattern == null) {
      throw new IllegalStateException("File name pattern required");
    }
    final FileHandler fileHandler = new FileHandler(pattern, limit, count, append);
    fileHandler.setErrorManager(errorManager);
    fileHandler.setFormatter(new ExtRecordFormatter(formatterPattern, formatterLogErrors));
    return new LoggerHandler(fileHandler, filter);
  }
}
