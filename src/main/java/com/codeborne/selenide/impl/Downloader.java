package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static com.codeborne.selenide.impl.DurationFormat.formatDuration;
import static com.codeborne.selenide.impl.FileHelper.ensureFolderExists;
import static java.lang.System.nanoTime;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

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

  public File copyFileWithTimeout(String fileName, FileSupplier action, long timeoutMs) {
    log.debug("Copying file {} with timeout {}", fileName, formatDuration(timeoutMs));
    long start = nanoTime();
    ExecutorService executor = newSingleThreadExecutor();
    try {
      File file = executor.submit(() -> copyFile(fileName, action, timeoutMs)).get(timeoutMs, MILLISECONDS);
      long executionTime = NANOSECONDS.toMillis(nanoTime() - start);
      log.debug("Copied file {} in {} (timeout was: {})", fileName, formatDuration(executionTime), formatDuration(timeoutMs));
      return file;
    }
    catch (TimeoutException | ExecutionException e) {
      throw new FileNotDownloadedError("Failed to copy downloaded file " + fileName, timeoutMs, e);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new FileNotDownloadedError("Failed to copy downloaded file " + fileName, timeoutMs, e);
    }
    finally {
      executor.shutdownNow();
    }
  }

  private File copyFile(String fileName, FileSupplier action, long timeoutMs) {
    try {
      return action.get();
    }
    catch (IOException e) {
      throw new FileNotDownloadedError("Failed to copy downloaded file " + fileName, timeoutMs, e);
    }
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

  @FunctionalInterface
  public interface FileSupplier {
    File get() throws IOException;
  }
}
