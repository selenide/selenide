package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;

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
}
