package com.codeborne.selenide;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.cleanDirectory;

/**
 * A unique folder per browser.
 * It effectively means that Selenide can delete all files in this folder before starting every new download.
 */
@ParametersAreNonnullByDefault
public class BrowserDownloadsFolder extends DownloadsFolder {
  public BrowserDownloadsFolder(File folder) {
    super(folder);
  }

  @Override
  public void cleanupBeforeDownload() {
    try {
      if (folder.exists()) {
        cleanDirectory(folder);
      }
    }
    catch (IOException e) {
      throw new IllegalStateException("Failed to cleanup folder " + folder.getAbsolutePath(), e);
    }
  }
}
