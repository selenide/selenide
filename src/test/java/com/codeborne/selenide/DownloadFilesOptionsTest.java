package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class DownloadFilesOptionsTest {
  @Test
  void factoryRequiresPositiveCount() {
    assertThatThrownBy(() -> files(0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("expectedFileCount must be >= 1");
    assertThatThrownBy(() -> files(-1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("expectedFileCount must be >= 1");
  }

  @Test
  void defaultOptions() {
    DownloadFilesOptions options = files(3);

    assertThat(options.expectedFileCount()).isEqualTo(3);
    assertThat(options.getMethod()).isNull();
    assertThat(options.timeout()).isNull();
    assertThat(options.getFilter()).isEqualTo(none());
  }

  @Test
  void withMethodPreservesCount() {
    DownloadFilesOptions options = files(3).withMethod(PROXY);

    assertThat(options.expectedFileCount()).isEqualTo(3);
    assertThat(options.getMethod()).isEqualTo(PROXY);
  }

  @Test
  void withTimeoutMillisPreservesCount() {
    DownloadFilesOptions options = files(2).withTimeout(9999);

    assertThat(options.expectedFileCount()).isEqualTo(2);
    assertThat(options.timeout()).isEqualTo(Duration.ofMillis(9999));
  }

  @Test
  void withTimeoutDurationPreservesCount() {
    DownloadFilesOptions options = files(2).withTimeout(Duration.ofSeconds(5));

    assertThat(options.expectedFileCount()).isEqualTo(2);
    assertThat(options.timeout()).isEqualTo(Duration.ofSeconds(5));
  }

  @Test
  void withFilterPreservesCount() {
    DownloadFilesOptions options = files(2).withExtension("pdf");

    assertThat(options.expectedFileCount()).isEqualTo(2);
    assertThat(options.getFilter()).usingRecursiveComparison().isEqualTo(withExtension("pdf"));
  }

  @Test
  void chainedSettings() {
    DownloadFilesOptions options = files(4)
      .withMethod(FOLDER)
      .withExtension("ppt")
      .withTimeout(Duration.ofMillis(1234));

    assertThat(options.expectedFileCount()).isEqualTo(4);
    assertThat(options.getMethod()).isEqualTo(FOLDER);
    assertThat(options.timeout()).isEqualTo(Duration.ofMillis(1234));
    assertThat(options.getFilter()).usingRecursiveComparison().isEqualTo(withExtension("ppt"));
  }

  @Test
  void printsOptionsToTestReport() {
    assertThat(files(3))
      .hasToString("expectedFileCount: 3");

    assertThat(files(3).withMethod(PROXY).withTimeout(9999))
      .hasToString("expectedFileCount: 3, method: PROXY, timeout: 9.999s");

    assertThat(files(2).withExtension("pdf"))
      .hasToString("expectedFileCount: 2, with extension \"pdf\"");
  }
}
