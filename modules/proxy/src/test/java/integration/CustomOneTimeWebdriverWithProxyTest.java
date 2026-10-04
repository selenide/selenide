package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.impl.FileContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.inNewBrowser;
import static org.assertj.core.api.Assertions.assertThat;

final class CustomOneTimeWebdriverWithProxyTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    Configuration.timeout = 4000;
  }

  @Test
  void canDownloadFilesViaProxy_inNewBrowser() {
    closeWebDriver();
    Configuration.proxyEnabled = true;
    openFile("page_with_uploads.html");

    inNewBrowser(() -> {
      openFile("downloadMultipleFiles.html");
      checkDownload(PROXY, "hello_world.*\\.txt", "hello_world.txt");
    });

    File downloadedFile = $(byText("Download me")).download(using(PROXY).withNameMatching("hello.*\\.txt"));
    assertThat(downloadedFile).hasName("hello_world.txt");
    assertThat(downloadedFile).content().isEqualToIgnoringNewLines("Hello, WinRar!");
  }

  private void checkDownload(FileDownloadMode mode, String fileName, String referenceFile) {
    File text = $("#multiple-downloads").download(using(mode).withNameMatching(fileName));
    assertThat(text.getName()).matches(fileName);
    assertThat(text.length()).isEqualTo(new FileContent(referenceFile).content().length());
  }
}
