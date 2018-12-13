package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;

import java.io.File;

public class Downloader {
  private final Randomizer random;

  public Downloader() {
    this(new Randomizer());
  }

  public Downloader(Randomizer random) {
    this.random = random;
  }

  public String randomFileName() {
    return random.text();
  }

  public File prepareTargetFile(Config config, String fileName) {
    File uniqueFolder = new File(config.reportsFolder(), random.text());
    if (uniqueFolder.exists()) {
      throw new IllegalStateException("Unbelievable! Unique folder already exists: " + uniqueFolder.getAbsolutePath());
    }
    if (!uniqueFolder.mkdirs()) {
      throw new RuntimeException("Failed to create folder " + uniqueFolder.getAbsolutePath());
    }

    return new File(uniqueFolder, fileName);
  }
}
