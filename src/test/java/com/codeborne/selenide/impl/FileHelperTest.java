package com.codeborne.selenide.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.impl.FileHelper.cleanupFolder;
import static com.codeborne.selenide.impl.FileHelper.deleteFolderIfEmpty;
import static java.nio.file.Files.createTempDirectory;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;

class FileHelperTest {
  private File folder;

  @BeforeEach
  void setUp() throws IOException {
    folder = createTempDirectory("FileHelperTest").toFile();
  }

  @AfterEach
  void tearDown() throws IOException {
    deleteDirectory(folder);
  }

  @Test
  void cleanupFolder_deletesAllFilesFromFolder() throws IOException {
    touch(new File(folder, "file1"));
    touch(new File(folder, "file2"));

    cleanupFolder(folder);

    assertThat(folder).exists();
    assertThat(new File(folder, "file1")).doesNotExist();
    assertThat(new File(folder, "file2")).doesNotExist();
  }

  @Test
  void deletesFolderIfItIsEmpty() {
    deleteFolderIfEmpty(folder);
    assertThat(folder).doesNotExist();
  }

  @Test
  void ignoresNullParameter() {
    deleteFolderIfEmpty(null);
  }

  @Test
  void ignoresFolderWhichContainsFiles() throws IOException {
    touch(new File(folder, "file1"));
    deleteFolderIfEmpty(folder);
    assertThat(folder).exists();
  }
}
