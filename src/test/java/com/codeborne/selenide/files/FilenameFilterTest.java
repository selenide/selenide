package com.codeborne.selenide.files;

import com.codeborne.selenide.proxy.DownloadedFile;
import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class FilenameFilterTest {
  private final FileFilter filter = new FilenameFilter("cv.pdf");

  @Test
  void matchesFileByName() {
    assertThat(filter.match(new DownloadedFile(new File("cv.pdf"), emptyMap()))).isTrue();
    assertThat(filter.match(new DownloadedFile(new File("cv1.pdf"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File(" cv.pdf"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File("cv.pdf "), emptyMap()))).isFalse();
  }

  @Test
  void description() {
    assertThat(filter.description()).isEqualTo(" with file name \"cv.pdf\"");
  }
}
