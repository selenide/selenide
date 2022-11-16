package com.codeborne.selenide.files;

import java.io.File;
import java.io.Serializable;

public interface FileFilter extends Serializable {
  boolean match(File file);
  String description();

  default boolean match(DownloadedFile file) {
    return match(file.getFile());
  }

  default boolean notMatch(File file) {
    return !match(file);
  }

  default boolean isEmpty() {
    return false;
  }
}
