package com.codeborne.selenide.files;

import com.codeborne.selenide.proxy.DownloadedFile;

class FilenameFilter implements FileFilter {
  private final String fileName;

  FilenameFilter(String fileName) {
    this.fileName = fileName;
  }

  @Override public boolean match(DownloadedFile file) {
    return file.getFile().getName().equals(fileName);
  }

  @Override public String description() {
    return " with file name \"" + fileName + "\"";
  }
}
