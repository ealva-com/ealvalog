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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.logging.FileHandler;

/**
 *
 *
 * Created by Eric A. Snell on 3/15/17.
 */
public class FileHandlerWrapper extends HandlerWrapper {
  public static FileHandlerBuilder fileBuilder() {
    return new FileHandlerBuilder();
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
}
