package com.codeborne.selenide.files;

import com.codeborne.selenide.proxy.DownloadedFile;

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
}
