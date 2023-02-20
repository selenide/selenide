package com.codeborne.selenide.files;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Set;

public class ExcludeBrowserOwnFilesFilter implements FileFilter {
  private final Set<String> browserInternalFiles = ImmutableSet.of("com.google.Chrome", "com.microsoft.edgemac");

  @Override public boolean match(File file) {
    String fileName = file.getName();
    return browserInternalFiles.stream().noneMatch(name -> fileName.contains(name));
  }

  @Override
  public boolean notMatch(File file) {
    return true;
  }

  @Override public String description() {
    return " with name other than \"" + browserInternalFiles + "\"";
  }

  @Override
  public String toString() {
    return description().trim();
  }

  public static FileFilter exceptBrowserOwnFiles() {
    return new ExcludeBrowserOwnFilesFilter();
  }
}
