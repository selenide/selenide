package org.selenide.grid;

import com.codeborne.selenide.impl.DownloadFileToFolder;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.impl.Plugins.inject;
import static org.assertj.core.api.Assertions.assertThat;

class DownloadFileFromGridToFolderTest {
  @Test
  void injectsTheRightImplementation() {
    DownloadFileToFolder downloader = inject(DownloadFileToFolder.class);
    assertThat(downloader).isInstanceOf(DownloadFileFromGridToFolder.class);
  }
}
