package com.codeborne.selenide;

import com.codeborne.selenide.files.DownloadAction;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static com.codeborne.selenide.DownloadOptions.ContentStrategy.FULL_CONTENT;
import static com.codeborne.selenide.DownloadOptions.file;
import static com.codeborne.selenide.DownloadOptions.files;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.files.DownloadActions.click;
import static com.codeborne.selenide.files.FileFilters.none;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;

@NullMarked
final class DownloadOptionsTest {
  @Test
  void defaultOptions() {
    DownloadOptions options = using(PROXY);

    assertThat(options.getMethod()).isEqualTo(PROXY);
    assertThat(options.timeout()).isNull();
    assertThat(options.incrementTimeout()).isNull();
    assertThat(options.getFilter()).isEqualTo(none());
    assertThat(options.getAction()).isEqualTo(click());
    assertThat(options.contentStrategy()).isEqualTo(FULL_CONTENT);
    assertThat(options.minimumFileCount()).isEqualTo(1);
  }

  @Test
  void customTimeout() {
    DownloadOptions options = using(PROXY).withTimeout(9999);

    assertThat(options.getMethod()).isEqualTo(PROXY);
    assertThat(options.timeout()).isEqualTo(Duration.ofMillis(9999));
    assertThat(options.incrementTimeout()).isNull();
    assertThat(options.getFilter()).isEqualTo(none());
  }

  @Test
  void customFileFilter() {
    DownloadOptions options = using(FOLDER).withExtension("pdf");

    assertThat(options.getMethod()).isEqualTo(FOLDER);
    assertThat(options.timeout()).isNull();
    assertThat(options.incrementTimeout()).isNull();
    assertThat(options.getFilter()).usingRecursiveComparison().isEqualTo(withExtension("pdf"));
  }

  @Test
  void customSettings() {
    DownloadOptions options = using(FOLDER).withExtension("ppt").withTimeout(Duration.ofMillis(1234));

    assertThat(options.getMethod()).isEqualTo(FOLDER);
    assertThat(options.timeout()).isEqualTo(Duration.ofMillis(1234));
    assertThat(options.incrementTimeout()).isNull();
    assertThat(options.getFilter()).usingRecursiveComparison().isEqualTo(withExtension("ppt"));
  }

  @Test
  void printsOptionsToTestReport() {
    assertThat(using(PROXY))
      .hasToString("method: PROXY");

    assertThat(using(PROXY).withTimeout(9999))
      .hasToString("method: PROXY, timeout: 9.999s");

    assertThat(using(PROXY).withTimeout(9999).withIncrementTimeout(ofMillis(2200)))
      .hasToString("method: PROXY, timeout: 9.999s, incrementTimeout: 2.2s");

    assertThat(using(HTTPGET).withTimeout(9999).withExtension("ppt"))
      .hasToString("method: HTTPGET, timeout: 9.999s, with extension \"ppt\"");

    assertThat(using(FOLDER).withFilter(withExtension("exe")))
      .hasToString("method: FOLDER, with extension \"exe\"");

    assertThat(using(FOLDER).withExtension("exe"))
      .hasToString("method: FOLDER, with extension \"exe\"");

    assertThat(file().withExtension("exe"))
      .hasToString("with extension \"exe\"");

    assertThat(file().withExtension("exe").withAction(new DoubleClick()))
      .hasToString("with extension \"exe\"");

    assertThat(files())
      .hasToString("minimumFileCount: 2");

    assertThat(files().withExtension("zip").withoutContent())
      .hasToString("minimumFileCount: 2, with extension \"zip\"");
  }

  private static class DoubleClick implements DownloadAction {
    @Override
    public void perform(Driver driver, WebElement link) {
      link.click();
      link.click();
    }

    @Override
    public String toString() {
      return "Double click";
    }
  }
}
