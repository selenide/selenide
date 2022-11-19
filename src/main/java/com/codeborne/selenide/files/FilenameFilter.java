package com.codeborne.selenide.files;

import java.io.File;

class FilenameFilter implements FileFilter {
  private final String fileName;

  FilenameFilter(String fileName) {
    this.fileName = fileName;
  }

  @Override public boolean match(File file) {
    return file.getName().equals(fileName);
  }

  @Override public String description() {
    return " with name \"" + fileName + "\"";
  }

  @Override
  public String toString() {
    return description().trim();
  }
}
