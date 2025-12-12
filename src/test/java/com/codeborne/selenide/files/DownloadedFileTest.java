package com.codeborne.selenide.files;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

final class DownloadedFileTest {
  @Test
  void hasContentDispositionHeader() {
    DownloadedFile file1 = new DownloadedFile(new File("x"), 0, 0, header("content-disposition", "filename=prices.csv"));
    DownloadedFile file2 = new DownloadedFile(new File("x"), 0, 0, header("", ""));
    assertThat(file1.hasContentDispositionHeader()).isTrue();
    assertThat(file2.hasContentDispositionHeader()).isFalse();
  }

  @Test
  void getContentType() {
    DownloadedFile file1 = new DownloadedFile(new File("x"), 0, 0, header("content-type", "application/pdf"));
    DownloadedFile file2 = new DownloadedFile(new File("x"), 0, 0, header("", ""));
    assertThat(file1.getContentType()).isEqualTo("application/pdf");
    assertThat(file2.getContentType()).isNull();
  }

  @Test
  void fileModificationCheck() throws IOException {
    assertThat(file(1597333000L).isFileModifiedLaterThan(1597333000L)).isTrue();
    assertThat(file(1597333000L).isFileModifiedLaterThan(1597332999L)).isTrue();
    assertThat(file(1597333000L).isFileModifiedLaterThan(1597334001L)).isFalse();
  }

  @Test
  void fileModificationCheck_worksWithSecondsPrecision() throws IOException {
    assertThat(file(1111111000L).isFileModifiedLaterThan(1111111000L)).isTrue();
    assertThat(file(1111111000L).isFileModifiedLaterThan(1111111999L)).isTrue();
    assertThat(file(1111111000L).isFileModifiedLaterThan(1111112000L)).isTrue();
    assertThat(file(1111111000L).isFileModifiedLaterThan(1111112001L)).isFalse();
  }

  @Test
  void fileModificationCheck_worksEvenIfFileModificationTime_isInPreviousSecond() throws IOException {
    assertThat(file(1111111112999L).isFileModifiedLaterThan(1111111113004L)).isTrue();
    assertThat(file(1111111114998L).isFileModifiedLaterThan(1111111115002L)).isTrue();
  }

  private DownloadedFile file(long modifiedAt) throws IOException {
//    File file = createTempFile("selenide-tests", "new-file");
//    FileUtils.touch(file);
//    if (!file.setLastModified(modifiedAt)) {
//      throw new IllegalStateException("Failed to set last modified time to file " + file.getAbsolutePath());
//    }
    return new DownloadedFile(new File(randomUUID().toString()), modifiedAt, 0, emptyMap());
  }

  private Map<String, String> header(String name, String value) {
    return Map.of(name, value);
  }
}
