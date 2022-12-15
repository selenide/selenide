package com.codeborne.selenide.proxy;

import com.browserup.bup.BrowserUpProxyServer;
import com.codeborne.selenide.Config;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class SelenideProxyServerTest {
  private final BrowserUpProxyServer bmp = mock();
  private final Config config = mock();
  private final SelenideProxyServer proxyServer = new SelenideProxyServer(config, null, bmp);

  @Test
  void canInterceptResponses() {
    proxyServer.start();

    verify(bmp).setTrustAllServers(true);
    verify(bmp, never()).setChainedProxy(any(InetSocketAddress.class));
    verify(bmp).start(0);

    FileDownloadFilter filter = proxyServer.responseFilter("download");
    assertThat(filter.downloads().files()).hasSize(0);

    FileDownloadFilter requestFilter = proxyServer.requestFilter("download");
    assertThat(requestFilter).isSameAs(filter);
  }

  @Test
  void canChainProxyServersWithNoProxySettings() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("127.0.0.1:3128");
    proxy.setNoProxy("localhost,https://example.com/");

    SelenideProxyServer proxyServer = new SelenideProxyServer(config, proxy, bmp);
    proxyServer.start();

    verify(bmp).setChainedProxy(any(InetSocketAddress.class));
    verify(bmp).setChainedProxyNonProxyHosts(Arrays.asList("localhost", "https://example.com/"));
    verify(bmp).start(0);
  }

  @Test
  @SuppressWarnings("unchecked")
  void canChainProxyServersWithEmptyNoProxySettings() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("127.0.0.1:3128");

    SelenideProxyServer proxyServer = new SelenideProxyServer(config, proxy, bmp);
    proxyServer.start();

    verify(bmp).setChainedProxy(any(InetSocketAddress.class));
    verify(bmp, never()).setChainedProxyNonProxyHosts(any(List.class));
    verify(bmp).start(0);
  }

  @Test
  void canShutdownProxyServer() {
    when(bmp.isStarted()).thenReturn(true);
    proxyServer.shutdown();
    verify(bmp).stop();
  }

  @Test
  void shouldNotShutdownProxyServer_ifItIsAlreadyStopped() {
    when(bmp.isStarted()).thenReturn(false);
    proxyServer.shutdown();
    verify(bmp, never()).abort();
  }

  @Test
  void createsSeleniumProxy() {
    when(bmp.getPort()).thenReturn(8888);
    proxyServer.start();

    assertThat(proxyServer.getSeleniumProxy()).isNotNull();
    assertThat(proxyServer.getSeleniumProxy().getHttpProxy()).endsWith(":8888");
  }

  @Test
  void createsSeleniumProxy_withConfiguredHostname() {
    when(config.proxyHost()).thenReturn("my.megahost");
    when(bmp.getPort()).thenReturn(9999);
    assertThat(proxyServer.getSeleniumProxy().getHttpProxy()).isEqualTo("my.megahost:9999");
  }

  @Test
  void extractsProxyAddress() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("111.22.3.4444:8080");

    InetSocketAddress proxyAddress = SelenideProxyServer.getProxyAddress(proxy);

    assertThat(proxyAddress.getHostName()).isEqualTo("111.22.3.4444");
    assertThat(proxyAddress.getPort()).isEqualTo(8080);
  }

  @Test
  void canStartProxyServerOnConfiguredPort() {
    when(config.proxyPort()).thenReturn(666);

    proxyServer.start();

    verify(bmp).start(666);
  }
}
