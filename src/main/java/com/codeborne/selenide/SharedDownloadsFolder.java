package com.codeborne.selenide;

import java.io.File;

public class SharedDownloadsFolder extends BrowserDownloadsFolder {
  public SharedDownloadsFolder(String folder) {
    super(new File(folder));
  }

  @Override
  public void cleanupBeforeDownload() {
  }

  @Override
  public void deleteIfEmpty() {
  }
}
