package com.codeborne.selenide.impl;

public class DummyFileNamer extends FileNamer {
  private final String fileName;

  public DummyFileNamer(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public String generateFileName() {
    return fileName;
  }
}
