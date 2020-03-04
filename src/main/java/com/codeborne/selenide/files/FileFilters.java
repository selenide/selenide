package com.codeborne.selenide.files;

public class FileFilters {
  private static final FileFilter NONE = new EmptyFileFilter();

  public static FileFilter none() {
    return NONE;
  }

  public static FileFilter withName(String fileName) {
    return new FilenameFilter(fileName);
  }

  public static FileFilter withNameMatching(String fileNameRegex) {
    return new FilenameRegexFilter(fileNameRegex);
  }

  public static FileFilter withExtension(String extension) {
    return new ExtensionFilter(extension);
  }
}
