package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;

final class SharedDownloadsFolderTest {
  @Test
  void shouldNotDeleteAnyFiles(@TempDir File folder) throws IOException {
    touch(new File(folder, "file1"));
    touch(new File(folder, "file2"));

    new SharedDownloadsFolder(folder.getAbsolutePath()).cleanupBeforeDownload();

    assertThat(new File(folder, "file1")).exists();
    assertThat(new File(folder, "file2")).exists();
  }
}
