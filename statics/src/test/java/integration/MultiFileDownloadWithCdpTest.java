package integration;

import com.codeborne.selenide.ex.FileNotDownloadedError;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.CDP;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

final class MultiFileDownloadWithCdpTest extends IntegrationTest {

  @BeforeEach
  void setUp() {
    assumeThat(isFirefox())
      .as("Firefox doesn't support CDP download method")
      .isFalse();
    if (SystemUtils.IS_OS_WINDOWS) {
      closeWebDriver();
    }
    openFile("downloadMultipleFiles.html");
  }

  @Test
  void downloadsAllThreeFiles() {
    List<File> downloaded = $("#multiple-downloads").downloadFiles(
      files(3).withMethod(CDP).withTimeout(ofSeconds(10))
    );

    assertThat(downloaded).hasSize(3);
    assertThat(downloaded).extracting(File::getName)
      .containsExactlyInAnyOrder("download.html", "empty.html", "hello_world.txt");
  }

  @Test
  void failsFastWhenMoreFilesThanExpected() {
    assertThatThrownBy(() ->
      $("#multiple-downloads").downloadFiles(
        files(2).withMethod(CDP).withTimeout(ofSeconds(10))
      )
    )
      .isInstanceOf(FileNotDownloadedError.class)
      .hasMessageContaining("Expected 2 files");
  }
}
