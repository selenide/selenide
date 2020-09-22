package com.codeborne.selenide.impl;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.file.Files.createDirectories;

@ParametersAreNonnullByDefault
public final class FileHelper {
  private static final Logger log = LoggerFactory.getLogger(FileHelper.class);

  private FileHelper() {
  }

  public static void writeToFile(byte[] source, File targetFile) throws IOException {
    try (InputStream in = new ByteArrayInputStream(source)) {
      copyFile(in, targetFile);
    }
  }

  public static void copyFile(File sourceFile, File targetFile) throws IOException {
    try (FileInputStream in = new FileInputStream(sourceFile)) {
      copyFile(in, targetFile);
    }
  }

  public static void copyFile(InputStream in, File targetFile) throws IOException {
    ensureParentFolderExists(targetFile);

    try (FileOutputStream out = new FileOutputStream(targetFile)) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
    }
  }

  public static void ensureParentFolderExists(File targetFile) {
    ensureFolderExists(targetFile.getParentFile());
  }

  @Nonnull
  public static File ensureFolderExists(File folder) {
    if (!folder.exists()) {
      log.info("Creating folder: {}", folder.getAbsolutePath());
      try {
        createDirectories(folder.toPath());
      } catch (IOException e) {
        throw new IllegalArgumentException("Failed to create folder '" + folder.getAbsolutePath() + "'", e);
      }
    }
    return folder;
  }

  public static void moveFile(File srcFile, File destFile) {
    try {
      FileUtils.moveFile(srcFile, destFile);
    }
    catch (IOException e) {
      throw new IllegalStateException("Failed to move file " + srcFile.getAbsolutePath() +
        " to " + destFile.getAbsolutePath(), e);
    }
  }

  public static void deleteFolderIfEmpty(@Nonnull File folder) {
    if (folder.isDirectory()) {
      File[] files = folder.listFiles();
      if (files == null || files.length == 0) {
        if (folder.delete()) {
          log.info("Deleted empty folder: {}", folder.getAbsolutePath());
        } else {
          log.error("Failed to delete empty folder: {}", folder.getAbsolutePath());
        }
      }
    }
  }
}
