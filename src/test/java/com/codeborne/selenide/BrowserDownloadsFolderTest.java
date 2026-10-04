package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class BrowserDownloadsFolderTest {
  @Test
  void deletesAllFilesFromFolder(@TempDir File folder) throws IOException {
    touch(new File(folder, "file1"));
    touch(new File(folder, "file2"));

    new BrowserDownloadsFolder(folder).cleanupBeforeDownload();

    assertThat(folder).exists();
    assertThat(new File(folder, "file1")).doesNotExist();
    assertThat(new File(folder, "file2")).doesNotExist();
  }

  @Test
  void ignoresFilesThatCannotBeDeleted(@TempDir File folder) throws IOException {
    touch(new File(folder, "file1"));
    File lockedFolder = new File(folder, "locked");
    touch(new File(lockedFolder, "file2.crdownload"));
    assumeTrue(lockedFolder.setExecutable(false) && lockedFolder.setReadable(false));

    try {
      new BrowserDownloadsFolder(folder).cleanupBeforeDownload();

      assertThat(new File(folder, "file1")).doesNotExist();
      assertThat(lockedFolder).exists();
    }
    finally {
      lockedFolder.setReadable(true);
      lockedFolder.setExecutable(true);
    }
  }
}
