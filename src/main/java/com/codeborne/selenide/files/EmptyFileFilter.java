package com.codeborne.selenide.files;

class EmptyFileFilter implements FileFilter {
  @Override
  public boolean match(DownloadedFile file) {
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
    return description();
  }
}
