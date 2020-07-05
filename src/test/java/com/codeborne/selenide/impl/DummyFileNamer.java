package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DummyFileNamer extends FileNamer {
  private final String fileName;

  public DummyFileNamer(String fileName) {
    this.fileName = fileName;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String generateFileName() {
    return fileName;
  }
}
