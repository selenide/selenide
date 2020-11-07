package com.codeborne.selenide.files;

import static org.apache.commons.io.FilenameUtils.isExtension;

class ExtensionFilter implements FileFilter {
  private final String extension;

  ExtensionFilter(String extension) {
    this.extension = extension;
  }

  @Override public boolean match(DownloadedFile file) {
    return isExtension(file.getFile().getName(), extension);
  }

  @Override public String description() {
    return " with extension \"" + extension + "\"";
  }
}
