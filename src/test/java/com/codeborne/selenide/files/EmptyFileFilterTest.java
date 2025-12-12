package com.codeborne.selenide.files;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.files.DownloadedFile.fileWithName;
import static org.assertj.core.api.Assertions.assertThat;

final class EmptyFileFilterTest {
  private final FileFilter filter = new EmptyFileFilter();

  @Test
  void matchesAnyFile() {
    assertThat(filter.match(fileWithName(""))).isTrue();
    assertThat(filter.match(fileWithName("cv.pdf"))).isTrue();
    assertThat(filter.match(fileWithName("cv1.pdf"))).isTrue();
    assertThat(filter.match(fileWithName(" cv.pdf"))).isTrue();
    assertThat(filter.match(fileWithName("cv.pdf "))).isTrue();
  }

  @Test
  void description() {
    assertThat(filter.description()).isEqualTo("");
  }

  @Test
  void hasToString() {
    assertThat(filter).hasToString("none");
  }

  @Test
  void isEmpty() {
    assertThat(filter.isEmpty()).isTrue();
  }
}
