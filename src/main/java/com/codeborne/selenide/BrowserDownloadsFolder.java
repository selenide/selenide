package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.impl.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.cleanDirectory;
import static org.apache.commons.io.FileUtils.listFiles;

/**
 * A unique folder per browser.
 * It effectively means that Selenide can delete all files in this folder before starting every new download.
 */
@ParametersAreNonnullByDefault
public class BrowserDownloadsFolder implements DownloadsFolder {
  private static final Logger log = LoggerFactory.getLogger(BrowserDownloadsFolder.class);
  private final File folder;

  protected BrowserDownloadsFolder(File folder) {
    this.folder = folder.getAbsoluteFile();
  }

  @Nonnull
  @CheckReturnValue
  public File getFolder() {
    return folder;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<File> files() {
    File[] files = folder.listFiles();
    return files == null ? emptyList() : asList(files);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public List<DownloadedFile> filesNewerThan(long modifiedAfterTs) {
    return files().stream()
      .filter(File::isFile)
      .filter(file -> isFileModifiedLaterThan(file, modifiedAfterTs))
      .map(file -> new DownloadedFile(file, emptyMap()))
      .collect(toList());
  }

  /**
   * Depending on OS, file modification time can have seconds precision, not milliseconds.
   * We have to ignore the difference in milliseconds.
   */
  static boolean isFileModifiedLaterThan(File file, long timestamp) {
    return file.lastModified() - timestamp >= -1000L;
  }

  @Nonnull
  @CheckReturnValue
  public File file(String fileName) {
    return new File(folder, fileName).getAbsoluteFile();
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

  @Override
  public String toString() {
    return folder.getPath();
  }

  @Nullable
  @CheckReturnValue
  public static BrowserDownloadsFolder from(@Nullable File folder) {
    return folder == null ? null : new BrowserDownloadsFolder(folder);
  }
}
