package com.codeborne.selenide.impl;

import com.codeborne.selenide.files.DownloadedFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class DownloadsMatchingFilesTest {

  @Test
  void matchingFilesReturnsAllMatchesSortedByMtimeAsc() {
    DownloadedFile newer = new DownloadedFile(new File("b.pdf"), 200L, 0, emptyMap());
    DownloadedFile older = new DownloadedFile(new File("a.pdf"), 100L, 0, emptyMap());
    DownloadedFile newest = new DownloadedFile(new File("c.pdf"), 300L, 0, emptyMap());
    Downloads downloads = new Downloads(List.of(newer, older, newest));

    List<DownloadedFile> result = downloads.matchingFiles(withExtension("pdf"));

    assertThat(result).extracting(DownloadedFile::getName).containsExactly("a.pdf", "b.pdf", "c.pdf");
  }

  @Test
  void matchingFilesBreaksTiesByName() {
    DownloadedFile b = new DownloadedFile(new File("b.pdf"), 100L, 0, emptyMap());
    DownloadedFile a = new DownloadedFile(new File("a.pdf"), 100L, 0, emptyMap());
    Downloads downloads = new Downloads(List.of(b, a));

    List<DownloadedFile> result = downloads.matchingFiles(withExtension("pdf"));

    assertThat(result).extracting(DownloadedFile::getName).containsExactly("a.pdf", "b.pdf");
  }

  @Test
  void matchingFilesAppliesFilter() {
    DownloadedFile pdf = new DownloadedFile(new File("a.pdf"), 100L, 0, emptyMap());
    DownloadedFile txt = new DownloadedFile(new File("b.txt"), 100L, 0, emptyMap());
    Downloads downloads = new Downloads(List.of(pdf, txt));

    List<DownloadedFile> result = downloads.matchingFiles(withExtension("pdf"));

    assertThat(result).extracting(DownloadedFile::getName).containsExactly("a.pdf");
  }

  @Test
  void matchingFilesEmpty() {
    Downloads downloads = new Downloads(List.of());

    assertThat(downloads.matchingFiles(none())).isEmpty();
  }

  @Test
  void matchingFilesAppliesFilterAndSortTogether() {
    DownloadedFile pdfNewer = new DownloadedFile(new File("b.pdf"), 200L, 0, emptyMap());
    DownloadedFile txt = new DownloadedFile(new File("c.txt"), 150L, 0, emptyMap());
    DownloadedFile pdfOlder = new DownloadedFile(new File("a.pdf"), 100L, 0, emptyMap());
    Downloads downloads = new Downloads(List.of(pdfNewer, txt, pdfOlder));

    List<DownloadedFile> result = downloads.matchingFiles(withExtension("pdf"));

    assertThat(result).extracting(DownloadedFile::getName).containsExactly("a.pdf", "b.pdf");
  }
}
