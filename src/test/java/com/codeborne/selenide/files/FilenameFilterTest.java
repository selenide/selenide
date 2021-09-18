package com.codeborne.selenide.files;

import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class FilenameFilterTest {
  private static final String FILTER_DESCRIPTION = "with file name \"cv.pdf\"";
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
    assertThat(filter.description()).isEqualTo(FILTER_DESCRIPTION);
  }

  @Test
  void hasToString() {
    assertThat(filter).hasToString(FILTER_DESCRIPTION);
  }

  @Test
  void isNotEmpty() {
    assertThat(filter.isEmpty()).isFalse();
  }
}
