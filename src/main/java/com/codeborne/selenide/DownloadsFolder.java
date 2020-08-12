package com.codeborne.selenide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@ParametersAreNonnullByDefault
public class DownloadsFolder {
  private static final Logger log = LoggerFactory.getLogger(DownloadsFolder.class);

  private final File folder;

  public DownloadsFolder(File folder) {
    this.folder = folder;
  }

  public DownloadsFolder(String folder) {
    this.folder = new File(folder);
  }

  public final String getAbsolutePath() {
    return folder.getAbsolutePath();
  }

  @CheckReturnValue
  @Nonnull
  public List<File> allDownloadedFiles() {
    File[] files = folder.listFiles();
    if (log.isDebugEnabled()) {
      log.debug("all downloaded files in {}: {}", folder.getAbsolutePath(), Arrays.toString(files));
    }
    return files == null ? emptyList() : asList(files);
  }
}
