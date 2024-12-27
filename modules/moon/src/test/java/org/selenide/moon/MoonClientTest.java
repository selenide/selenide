package org.selenide.moon;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class MoonClientTest {
  @Test
  void urlForAccessingClipboard() {
    MoonClient client = new MoonClient("http://moon.aerokube.local/wd/hub", "chrome-124-0-d6e");
    assertThat(client.clipboardAccessUrl().toExternalForm())
      .isEqualTo("http://moon.aerokube.local/wd/hub/session/chrome-124-0-d6e/aerokube/clipboard");
  }

  @Test
  void urlForAccessingDownloadedFiles() {
    MoonClient client = new MoonClient("http://moon.aerokube.local/wd/hub", "chrome-124-0-d6e");
    assertThat(client.downloadedFilesAccessUrl().toExternalForm())
      .isEqualTo("http://moon.aerokube.local/wd/hub/session/chrome-124-0-d6e/aerokube/download");
  }

  @Test
  void encodesFileNameInUrl() throws MalformedURLException {
    MoonClient client = new MoonClient("http://localhost:4444/wd/hub", "sid-01");
    assertThat(client.urlOfDownloadedFile("some-file.txt")).isEqualTo(
        new URL("http://localhost:4444/wd/hub/session/sid-01/aerokube/download/some-file.txt")
    );
    assertThat(client.urlOfDownloadedFile("some file (2).txt")).isEqualTo(
        new URL("http://localhost:4444/wd/hub/session/sid-01/aerokube/download/some%20file%20%282%29.txt")
    );
  }
}
