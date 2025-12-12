package com.codeborne.selenide.files;

import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ExtensionFilterTest {
  private static final String FILTER_DESCRIPTION = "with extension \"pdf\"";
  private final FileFilter filter = new ExtensionFilter("pdf");

  @Test
  void matchesFileByExtension() {
    assertThat(filter.match(file("cv.pdf"))).isTrue();
    assertThat(filter.match(file("report.pdf"))).isTrue();
    assertThat(filter.match(file("report.PDF"))).isFalse();
    assertThat(filter.match(file("cv.pdff"))).isFalse();
    assertThat(filter.match(file("cv.ppdf"))).isFalse();
    assertThat(filter.match(file("cv.pdf.gz"))).isFalse();
    assertThat(filter.match(file("cv.xpdf"))).isFalse();
    assertThat(filter.match(file("cv.pdfx"))).isFalse();
  }

  private DownloadedFile file(String name) {
    return new DownloadedFile(new File(name), 0, 0, emptyMap());
  }

  @Test
  void ignoresDotInExtension() {
    assertThatThrownBy(() -> new ExtensionFilter(".pdf"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("File extension cannot contain dot: '.pdf'");
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
}
