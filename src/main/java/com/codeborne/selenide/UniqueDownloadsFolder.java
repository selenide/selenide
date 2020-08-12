package com.codeborne.selenide;

import com.codeborne.selenide.impl.FileHelper;

import java.io.File;

/**
 * A unique folder per browser.
 * It effectively means that Selenide can delete all files in this folder before starting every new download.
 */
public class UniqueDownloadsFolder extends DownloadsFolder {
  public UniqueDownloadsFolder(File folder) {
    super(folder);
  }

  @Override
  public void cleanupBeforeDownload() {
    FileHelper.cleanupFolder(folder);
  }
}
