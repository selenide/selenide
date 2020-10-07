package com.codeborne.selenide.files;

import com.codeborne.selenide.proxy.DownloadedFile;
import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class FilenameRegexFilterTest {
  private final FileFilter filter = new FilenameRegexFilter("cv-\\d+.pdf");

  @Test
  void matchesFileByNameUsingGivenRegularExpression() {
    assertThat(filter.match(new DownloadedFile(new File("cv-100.pdf"), emptyMap()))).isTrue();
    assertThat(filter.match(new DownloadedFile(new File("cv100.pdf"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File("cv.pdf"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File("cv-ten.pdf"), emptyMap()))).isFalse();
  }

  @Test
  void description() {
    assertThat(filter.description()).isEqualTo(" with file name matching \"cv-\\d+.pdf\"");
  }
}
