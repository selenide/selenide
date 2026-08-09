package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.impl.FileHelper.deleteFolderIfEmpty;
import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class FileHelperTest {
  @TempDir
  File reportsFolder;

  @Test
  void deletesFolderIfItIsEmpty(@TempDir File folder) {
    assertThat(folder).exists();

    deleteFolderIfEmpty(folder);

    assertThat(folder).doesNotExist();
  }

  @Test
  void ignoresFolderWhichContainsFiles(@TempDir File folder) throws IOException {
    touch(new File(folder, "file1"));

    deleteFolderIfEmpty(folder);

    assertThat(folder).exists();
    assertThat(new File(folder, "file1")).exists();
  }

  @Test
  void fileInFolder_allowsSubdirectories() {
    File file = FileHelper.fileInFolder(reportsFolder, "com/example/Test.123", "mhtml");

    assertThat(file.getPath()).endsWith("com" + File.separator + "example" + File.separator + "Test.123.mhtml");
    assertThat(file.getAbsolutePath()).startsWith(reportsFolder.getAbsolutePath());
  }

  @Test
  void fileInFolder_rejectsPathTraversal() {
    assertThatThrownBy(() -> FileHelper.fileInFolder(reportsFolder, "../../outside", "html"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid file name: ../../outside");
  }

  @Test
  void fileInFolder_rejectsSiblingFolderWithSamePrefix() {
    assertThatThrownBy(() -> FileHelper.fileInFolder(reportsFolder,
      "../" + reportsFolder.getName() + "-other/escaped", "html"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid file name: ../" + reportsFolder.getName() + "-other/escaped");
  }
}
