package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static org.assertj.core.api.Assertions.assertThat;

final class DownloadOptionsTest {
  @Test
  void defaultOptions() {
    DownloadOptions options = using(PROXY);

    assertThat(options.getMethod()).isEqualTo(PROXY);
    assertThat(options.getTimeout(4000)).isEqualTo(4000);
    assertThat(options.getFilter()).isEqualTo(none());
  }

  @Test
  void customTimeout() {
    DownloadOptions options = using(PROXY).withTimeout(9999);

    assertThat(options.getMethod()).isEqualTo(PROXY);
    assertThat(options.getTimeout(4000)).isEqualTo(9999);
    assertThat(options.getFilter()).isEqualTo(none());
  }

  @Test
  void customFileFilter() {
    DownloadOptions options = using(FOLDER).withFilter(withExtension("pdf"));

    assertThat(options.getMethod()).isEqualTo(FOLDER);
    assertThat(options.getTimeout(8000)).isEqualTo(8000);
    assertThat(options.getFilter()).usingRecursiveComparison().isEqualTo(withExtension("pdf"));
  }

  @Test
  void customSettings() {
    DownloadOptions options = using(FOLDER).withFilter(withExtension("ppt")).withTimeout(1234);

    assertThat(options.getMethod()).isEqualTo(FOLDER);
    assertThat(options.getTimeout(4000)).isEqualTo(1234);
    assertThat(options.getFilter()).usingRecursiveComparison().isEqualTo(withExtension("ppt"));
  }
}
