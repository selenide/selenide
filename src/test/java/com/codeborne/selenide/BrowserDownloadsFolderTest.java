package com.codeborne.selenide;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.BrowserDownloadsFolder.isFileModifiedLaterThan;
import static java.io.File.createTempFile;
import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;

final class BrowserDownloadsFolderTest {
  @Test
  void deletesAllFilesFromFolder(@TempDir File folder) throws IOException {
    touch(new File(folder, "file1"));
    touch(new File(folder, "file2"));

    BrowserDownloadsFolder.from(folder).cleanupBeforeDownload();

    assertThat(folder).exists();
    assertThat(new File(folder, "file1")).doesNotExist();
    assertThat(new File(folder, "file2")).doesNotExist();
  }

  @Test
  void fileModificationCheck() throws IOException {
    assertThat(isFileModifiedLaterThan(file(1597333000L), 1597333000L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1597333000L), 1597332999L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1597333000L), 1597334001L)).isFalse();
  }

  @Test
  void fileModificationCheck_worksWithSecondsPrecision() throws IOException {
    assertThat(isFileModifiedLaterThan(file(1111111000L), 1111111000L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1111111000L), 1111111999L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1111111000L), 1111112000L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1111111000L), 1111112001L)).isFalse();
  }

  @Test
  void fileModificationCheck_worksEvenIfFileModificationTime_isInPreviousSecond() throws IOException {
    assertThat(isFileModifiedLaterThan(file(1111111112999L), 1111111113004L)).isTrue();
    assertThat(isFileModifiedLaterThan(file(1111111114998L), 1111111115002L)).isTrue();
  }

  private File file(long modifiedAt) throws IOException {
    File file = createTempFile("selenide-tests", "new-file");
    FileUtils.touch(file);
    if (!file.setLastModified(modifiedAt)) {
      throw new IllegalStateException("Failed to set last modified time to file " + file.getAbsolutePath());
    }
    return file;
  }

}
