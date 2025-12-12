package com.codeborne.selenide.files;

import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class FilenameRegexFilterTest {
  private static final String FILTER_DESCRIPTION = "with name matching \"cv-\\d+.pdf\"";
  private final FileFilter filter = new FilenameRegexFilter("cv-\\d+.pdf");

  @Test
  void matchesFileByNameUsingGivenRegularExpression() {
    assertThat(filter.match(file("cv-100.pdf"))).isTrue();
    assertThat(filter.match(file("cv100.pdf"))).isFalse();
    assertThat(filter.match(file("cv.pdf"))).isFalse();
    assertThat(filter.match(file("cv-ten.pdf"))).isFalse();
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
