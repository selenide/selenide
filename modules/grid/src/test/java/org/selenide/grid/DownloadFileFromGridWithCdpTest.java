package org.selenide.grid;

import com.codeborne.selenide.impl.DownloadFileWithCdp;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.impl.Plugins.inject;
import static org.assertj.core.api.Assertions.assertThat;

class DownloadFileFromGridWithCdpTest {
  @Test
  void injectsTheRightImplementation() {
    DownloadFileWithCdp downloader = inject(DownloadFileWithCdp.class);
    assertThat(downloader).isInstanceOf(DownloadFileFromGridWithCdp.class);
  }
}
