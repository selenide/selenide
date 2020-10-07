package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.impl.FileHelper.deleteFolderIfEmpty;
import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;

final class FileHelperTest {
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
}
