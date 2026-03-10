package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Downloader {
  private static final Logger log = LoggerFactory.getLogger(Downloader.class);
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

  public File mockFileContent(Config config, String fileName) {
    File uniqueFolder = prepareTargetFolder(config);
    File archivedFile = new File(uniqueFolder, fileName);
    mockFileContent(archivedFile);
    return archivedFile;
  }

  @CanIgnoreReturnValue
  public File mockFileContent(File localFile) {
    try {
      FileUtils.writeStringToFile(localFile, "Mocked file content",  UTF_8);
    }
    catch (IOException e) {
      log.error("Failed to write content to local file {}", localFile, e);
    }
    return localFile;
  }
}
