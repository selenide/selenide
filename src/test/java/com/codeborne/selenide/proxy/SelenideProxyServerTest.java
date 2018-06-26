package com.codeborne.selenide.proxy;

import java.net.InetSocketAddress;

import net.lightbody.bmp.BrowserMobProxyServer;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelenideProxyServerTest {
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
      MatcherAssert.assertThat(proxyServer.createSeleniumProxy().getHttpProxy(), endsWith(":8888"));
    } finally {
      proxyServer.shutdown();
    }
    verify(bmp).abort();

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    MatcherAssert.assertThat(filter.getDownloadedFiles().size(), is(0));
  }

  @Test
  void extractsProxyAddress() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("111.22.3.4444:8080");

    InetSocketAddress proxyAddress = SelenideProxyServer.getProxyAddress(proxy);

    Assertions.assertEquals("111.22.3.4444", proxyAddress.getHostName());
    Assertions.assertEquals(8080, proxyAddress.getPort());
  }
}
