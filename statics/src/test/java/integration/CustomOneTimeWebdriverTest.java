package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.impl.FileContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.inNewBrowser;
import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.files.FileFilters.withExtension;
import static com.codeborne.selenide.files.FileFilters.withNameMatching;
import static org.assertj.core.api.Assertions.assertThat;

final class CustomOneTimeWebdriverTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    Configuration.timeout = 4000;
  }

  @Test
  void canOpen_oneTimeBrowser_withSameSettingsAsDefaultBrowser() {
    openFile("page_with_big_divs.html");
    WebDriver defaultBrowser = getWebDriver();
    $("h1").shouldHave(text("Some big divs"));

    inNewBrowser(() -> {
      openFile("page_with_selects_without_jquery.html");
      $("h1").shouldHave(text("Page with selects"));
      assertThat(getWebDriver()).isNotSameAs(defaultBrowser);
    });

    assertThat(WebDriverRunner.hasWebDriverStarted()).isTrue();
    $("h1").shouldHave(text("Some big divs"));
  }

  @Test
  void canDownloadFilesToFolder_inNewBrowser() throws IOException {
    openFile("page_with_uploads.html");

    inNewBrowser(() -> {
      openFile("downloadMultipleFiles.html");
      checkDownload(FOLDER);
    });

    File downloadedFile = $(byText("Download me")).download(using(FOLDER).withFilter(withExtension("txt")));
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  @Test
  void canDownloadFilesViaProxy_inNewBrowser() throws IOException {
    closeWebDriver();
    Configuration.proxyEnabled = true;
    openFile("page_with_uploads.html");

    inNewBrowser(() -> {
      openFile("downloadMultipleFiles.html");
      checkDownload(PROXY, "hello_world.*\\.txt", "hello_world.txt");
    });

    File downloadedFile = $(byText("Download me")).download(using(PROXY).withFilter(withExtension("txt")));
    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  private void checkDownload(FileDownloadMode mode) {
    checkDownload(mode, "download.*\\.html", "download.html");
    checkDownload(mode, "empty.*\\.html", "empty.html");
    checkDownload(mode, "hello_world.*\\.txt", "hello_world.txt");
  }

  private void checkDownload(FileDownloadMode mode, String fileName, String referenceFile) {
    try {
      File text = $("#multiple-downloads").download(
        using(mode).withFilter(withNameMatching(fileName))
      );
      assertThat(text.getName()).matches(fileName);
      assertThat(text.length()).isEqualTo(new FileContent(referenceFile).content().length());
    }
    catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
