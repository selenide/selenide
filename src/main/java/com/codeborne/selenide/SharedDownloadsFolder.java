package com.codeborne.selenide;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public class SharedDownloadsFolder extends DownloadsFolder {
  public SharedDownloadsFolder(String folder) {
    super(new File(folder));
  }

  @Override
  public void cleanupBeforeDownload() {
  }
}
