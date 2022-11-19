package com.codeborne.selenide.files;

import java.io.File;
import java.util.regex.Pattern;

class FilenameRegexFilter implements FileFilter {
  private final Pattern fileNameRegex;

  FilenameRegexFilter(String fileNameRegex) {
    this.fileNameRegex = Pattern.compile(fileNameRegex);
  }

  @Override public boolean match(File file) {
    return fileNameRegex.matcher(file.getName()).matches();
  }

  @Override public String description() {
    return " with name matching \"" + fileNameRegex + "\"";
  }

  @Override
  public String toString() {
    return description().trim();
  }
}
