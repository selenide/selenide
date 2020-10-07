package com.codeborne.selenide.files;

import com.codeborne.selenide.proxy.DownloadedFile;
import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class ExtensionFilterTest {
  private final FileFilter filter = new ExtensionFilter("pdf");

  @Test
  void matchesFileByExtension() {
    assertThat(filter.match(new DownloadedFile(new File("cv.pdf"), emptyMap()))).isTrue();
    assertThat(filter.match(new DownloadedFile(new File("report.pdf"), emptyMap()))).isTrue();
    assertThat(filter.match(new DownloadedFile(new File("report.PDF"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File("cv.pdff"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File("cv.ppdf"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File("cv.pdf.gz"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File("cv.xpdf"), emptyMap()))).isFalse();
    assertThat(filter.match(new DownloadedFile(new File("cv.pdfx"), emptyMap()))).isFalse();
  }

  @Test
  void description() {
    assertThat(filter.description()).isEqualTo(" with extension \"pdf\"");
  }

}
