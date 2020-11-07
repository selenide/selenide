package com.codeborne.selenide.files;

import java.util.regex.Pattern;

class FilenameRegexFilter implements FileFilter {
  private final Pattern fileNameRegex;

  FilenameRegexFilter(String fileNameRegex) {
    this.fileNameRegex = Pattern.compile(fileNameRegex);
  }

  @Override public boolean match(DownloadedFile file) {
    return fileNameRegex.matcher(file.getFile().getName()).matches();
  }

  @Override public String description() {
    return " with file name matching \"" + fileNameRegex + "\"";
  }
}
