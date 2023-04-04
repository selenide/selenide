package com.codeborne.selenide;

import com.codeborne.selenide.impl.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.cleanDirectory;
import static org.apache.commons.io.FileUtils.listFiles;

/**
 * A unique folder per browser.
 * It effectively means that Selenide can delete all files in this folder before starting every new download.
 */
@ParametersAreNonnullByDefault
public final class BrowserDownloadsFolder extends DownloadsFolder {
  private static final Logger log = LoggerFactory.getLogger(BrowserDownloadsFolder.class);
  private BrowserDownloadsFolder(File folder) {
    super(folder);
  }

  @Override
  public void cleanupBeforeDownload() {
    try {
      if (folder.exists()) {
        log.debug("Going to clean folder {} - found files: {}", folder, filesIn(folder));
        cleanDirectory(folder);
        log.debug("After clean folder {}: {}", folder, filesIn(folder));
      }
      else {
        log.debug("Folder {} doesn't exist. Nothing to clean up.", folder);
      }
    }
    catch (IOException e) {
      throw new IllegalStateException("Failed to cleanup folder " + folder.getAbsolutePath(), e);
    }
  }

  private Collection<String> filesIn(File dir) {
    return listFiles(dir, null, false).stream().map(f -> f.getName()).collect(toList());
  }

  @Override
  public void deleteIfEmpty() {
    FileHelper.deleteFolderIfEmpty(folder);
  }

  @Nullable
  @CheckReturnValue
  public static BrowserDownloadsFolder from(@Nullable File folder) {
    return folder == null ? null : new BrowserDownloadsFolder(folder);
  }
}
