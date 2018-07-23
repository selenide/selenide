package com.codeborne.selenide.proxy;

import java.net.InetSocketAddress;

import net.lightbody.bmp.BrowserMobProxyServer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelenideProxyServerTest implements WithAssertions {
  @Test
  void canInterceptResponses() {
    BrowserMobProxyServer bmp = mock(BrowserMobProxyServer.class);
    when(bmp.getPort()).thenReturn(8888);

    SelenideProxyServer proxyServer = new SelenideProxyServer(null);
    proxyServer.proxy = bmp;
    proxyServer.start();

    try {
      verify(bmp).setTrustAllServers(true);
      verify(bmp, never()).setChainedProxy(any(InetSocketAddress.class));
      verify(bmp).start();
      assertThat(proxyServer.createSeleniumProxy().getHttpProxy())
        .endsWith(":8888");
    } finally {
      proxyServer.shutdown();
    }
    verify(bmp).abort();

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    assertThat(filter.getDownloadedFiles().size())
      .isEqualTo(0);
  }

  @Test
  void extractsProxyAddress() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("111.22.3.4444:8080");

    InetSocketAddress proxyAddress = SelenideProxyServer.getProxyAddress(proxy);

    assertThat(proxyAddress.getHostName())
      .isEqualTo("111.22.3.4444");
    assertThat(proxyAddress.getPort())
      .isEqualTo(8080);
  }
}
