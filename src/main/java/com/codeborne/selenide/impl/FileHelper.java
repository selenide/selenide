package com.codeborne.selenide.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.FileUtils.cleanDirectory;

@ParametersAreNonnullByDefault
public final class FileHelper {
  private static final Logger log = LoggerFactory.getLogger(FileHelper.class);

  private FileHelper() {
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
      if (!folder.mkdirs()) {
        log.error("Failed to create folder: {}", folder.getAbsolutePath());
      }
    }
    return folder;
  }

  public static void cleanupFolder(File folder) throws IOException {
    if (folder.isDirectory()) {
      cleanDirectory(folder);
    }
  }
}
