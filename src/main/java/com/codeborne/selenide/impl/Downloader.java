package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;

import java.io.File;

import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;

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
    File uniqueFolder = prepareTargetFolder(config);
    return new File(uniqueFolder, fileName);
  }

  public File prepareTargetFolder(Config config) {
    File parent = new File(config.downloadsFolder()).getAbsoluteFile();
    File uniqueFolder = new File(parent, random.text());
    if (uniqueFolder.exists()) {
      throw new IllegalStateException("Unbelievable! Unique folder already exists: " + uniqueFolder);
    }
    ensureFolderExists(uniqueFolder);
    return uniqueFolder;
  }
}
