package com.codeborne.selenide.files;

import com.codeborne.selenide.proxy.DownloadedFile;

import java.io.Serializable;

public interface FileFilter extends Serializable {
  boolean match(DownloadedFile file);
  String description();

  default boolean isEmpty() {
    return false;
  }
}
