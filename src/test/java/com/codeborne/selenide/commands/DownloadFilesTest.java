package com.codeborne.selenide.commands;

import com.codeborne.selenide.DownloadFilesOptions;
import com.codeborne.selenide.impl.DownloadFileToFolder;
import com.codeborne.selenide.impl.DownloadFileWithCdp;
import com.codeborne.selenide.impl.DownloadFileWithHttpRequest;
import com.codeborne.selenide.impl.DownloadFileWithProxyServer;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.DownloadFilesOptions.files;
import static com.codeborne.selenide.FileDownloadMode.CDP;
import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static com.codeborne.selenide.FileDownloadMode.HTTPGET;
import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

final class DownloadFilesTest {
  private final DownloadFileWithHttpRequest httpGet = mock();
  private final DownloadFileWithProxyServer proxy = mock();
  private final DownloadFileToFolder folder = mock();
  private final DownloadFileWithCdp cdp = mock();
  private final DownloadFiles command = new DownloadFiles(httpGet, proxy, folder, cdp);

  @Test
  void httpgetRejectsMultiFile() {
    DownloadFilesOptions options = files(2).withMethod(HTTPGET);

    assertThatThrownBy(() -> command.validateMode(options))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("HTTPGET")
      .hasMessageContaining("FOLDER")
      .hasMessageContaining("CDP")
      .hasMessageContaining("PROXY");
  }

  @Test
  void httpgetAllowsSingleFile() {
    DownloadFilesOptions options = files(1).withMethod(HTTPGET);
    // Should not throw
    command.validateMode(options);
  }

  @Test
  void folderAllowsMultiFile() {
    command.validateMode(files(5).withMethod(FOLDER));
    command.validateMode(files(5).withMethod(CDP));
    command.validateMode(files(5).withMethod(PROXY));
  }
}
