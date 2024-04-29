package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Configuration.proxyEnabled;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.DownloadOptions.using;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class FileDownloadWithoutProxyTest extends IntegrationTest {
  @BeforeEach
  void setUp() {
    proxyEnabled = false;
    timeout = 1000;
    openFile("page_with_uploads.html");
  }

  @Test
  public void cannotDownloadUsingProxy_ifBrowserIsOpenedWithoutProxy() {
    assertThatThrownBy(() -> $(byText("Download me")).download(using(PROXY)))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageStartingWith("Cannot download file: proxy server is not enabled. Setup proxyEnabled");
  }

}

