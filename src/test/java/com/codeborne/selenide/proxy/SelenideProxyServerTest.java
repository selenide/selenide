package com.codeborne.selenide.proxy;

import net.lightbody.bmp.BrowserMobProxyServer;
import org.junit.Test;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SelenideProxyServerTest {
  @Test
  public void canInterceptResponses() {
    BrowserMobProxyServer bmp = mock(BrowserMobProxyServer.class);
    when(bmp.getPort()).thenReturn(8888);

    SelenideProxyServer proxyServer = new SelenideProxyServer(null);
    proxyServer.proxy = bmp;
    proxyServer.start();

    try {
      verify(bmp).setTrustAllServers(true);
      verify(bmp, never()).setChainedProxy(any(InetSocketAddress.class));
      verify(bmp).start();
      assertThat(proxyServer.createSeleniumProxy().getHttpProxy(), endsWith(":8888"));
    }
    finally {
      proxyServer.shutdown();
    }
    verify(bmp).abort();

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    assertThat(filter.getDownloadedFiles().size(), is(0));
  }

  @Test
  public void extractsProxyAddress() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("111.22.3.4444:8080");

    InetSocketAddress proxyAddress = SelenideProxyServer.getProxyAddress(proxy);

    assertEquals("111.22.3.4444", proxyAddress.getHostName());
    assertEquals(8080, proxyAddress.getPort());
  }
}
