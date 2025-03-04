package org.selenide.selenoid;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelenoidClientTest {
  @Test
  void extractsSelenoidBaseUrlFromHubUrl() {
    SelenoidClient client = new SelenoidClient("http://localhost:4444/wd/hub", "sid-01");
    assertThat(client.baseUrl).isEqualTo("http://localhost:4444");
  }

  @Test
  void encodesFileNameInUrl() throws MalformedURLException {
    SelenoidClient client = new SelenoidClient("http://localhost:4444/wd/hub", "sid-01");
    assertThat(client.urlOfDownloadedFile("some-file.txt")).isEqualTo(
      new URL("http://localhost:4444/download/sid-01/some-file.txt")
    );
    assertThat(client.urlOfDownloadedFile("some file (2).txt")).isEqualTo(
      new URL("http://localhost:4444/download/sid-01/some%20file%20%282%29.txt")
    );
  }

  @Test
  void validatesFileName() {
    SelenoidClient client = new SelenoidClient("", "");
    assertThatThrownBy(() -> client.urlOfDownloadedFile("../../etc/hosts"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid file name: ../../etc/hosts");
    assertThatThrownBy(() -> client.urlOfDownloadedFile("/etc/hosts"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid file name: /etc/hosts");
    assertThatThrownBy(() -> client.urlOfDownloadedFile(".../...//etc/hosts"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid file name: .../...//etc/hosts");
  }

  @Test
  void readsFileNamesFromJson() {
    String selenoidDownloadsResponse = "[\"test.txt\", \"report.pdf\"]";
    SelenoidClient client = new SelenoidClient("http://localhost:4444/wd/hub", "sid-01");
    assertThat(client.parseJson(selenoidDownloadsResponse)).containsExactly(
      "test.txt", "report.pdf"
    );
  }
}
