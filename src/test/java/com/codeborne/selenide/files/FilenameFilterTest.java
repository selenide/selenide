package com.codeborne.selenide.files;

import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class FilenameFilterTest {
  private static final String FILTER_DESCRIPTION = "with name \"cv.pdf\"";
  private final FileFilter filter = new FilenameFilter("cv.pdf");

  @Test
  void matchesFileByName() {
    assertThat(filter.match(file("cv.pdf"))).isTrue();
    assertThat(filter.match(file("cv1.pdf"))).isFalse();
    assertThat(filter.match(file(" cv.pdf"))).isFalse();
    assertThat(filter.match(file("cv.pdf "))).isFalse();
  }

  @Test
  void description() {
    assertThat(filter.description()).isEqualTo(' ' + FILTER_DESCRIPTION);
  }

  @Test
  void hasToString() {
    assertThat(filter).hasToString(FILTER_DESCRIPTION);
  }

  @Test
  void isNotEmpty() {
    assertThat(filter.isEmpty()).isFalse();
  }

  private DownloadedFile file(String name) {
    return new DownloadedFile(new File(name), 0, 0, emptyMap());
  }
}
