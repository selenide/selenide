package com.codeborne.selenide.files;

import java.io.File;

class EmptyFileFilter implements FileFilter {
  @Override
  public boolean match(File file) {
    return true;
  }

  @Override
  public boolean notMatch(File file) {
    return true;
  }

  @Override
  public String description() {
    return "";
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public String toString() {
    return "none";
  }
}
