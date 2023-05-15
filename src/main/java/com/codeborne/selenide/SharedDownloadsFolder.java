package com.codeborne.selenide;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
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
