package com.codeborne.selenide.proxy;

import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.filters.RequestFilter;
import com.browserup.bup.filters.ResponseFilter;
import com.codeborne.selenide.Config;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

  @Test
  void canGetRequestFilters() {
    proxyServer.start();

    proxyServer.requestFilterNames()
      .forEach(proxyServer::removeRequestFilter);

    RequestFilter emptyRequestFilter = (request, contents, messageInfo) -> null;

    proxyServer.addRequestFilter("foo-request-filter-1", emptyRequestFilter);
    proxyServer.addRequestFilter("foo-request-filter-2", emptyRequestFilter);
    proxyServer.addRequestFilter("foo-request-filter-3", emptyRequestFilter);
    proxyServer.addRequestFilter("bar-request-filter-1", emptyRequestFilter);
    proxyServer.addRequestFilter("bar-request-filter-2", emptyRequestFilter);
    proxyServer.addRequestFilter("baz-request-filter-1", emptyRequestFilter);

    Map<String, RequestFilter> requestFilters = proxyServer.requestFilters();
    assertThat(requestFilters)
      .hasSize(6)
      .isEqualTo(Map.of(
        "foo-request-filter-1", emptyRequestFilter,
        "foo-request-filter-2", emptyRequestFilter,
        "foo-request-filter-3", emptyRequestFilter,
        "bar-request-filter-1", emptyRequestFilter,
        "bar-request-filter-2", emptyRequestFilter,
        "baz-request-filter-1", emptyRequestFilter
      ));

    proxyServer.requestFilterNames()
      .stream()
      .filter(filterName -> filterName.startsWith("foo"))
      .forEach(proxyServer::removeRequestFilter);

    Map<String, RequestFilter> updatedRequestFilters = proxyServer.requestFilters();
    assertThat(updatedRequestFilters)
      .hasSize(3)
      .isEqualTo(Map.of(
        "bar-request-filter-1", emptyRequestFilter,
        "bar-request-filter-2", emptyRequestFilter,
        "baz-request-filter-1", emptyRequestFilter
      ));
  }

  @Test
  void canGetRequestFilterNames() {
    proxyServer.start();

    proxyServer.requestFilterNames()
      .forEach(proxyServer::removeRequestFilter);

    RequestFilter emptyRequestFilter = (request, contents, messageInfo) -> null;

    proxyServer.addRequestFilter("foo-request-filter-1", emptyRequestFilter);
    proxyServer.addRequestFilter("foo-request-filter-2", emptyRequestFilter);
    proxyServer.addRequestFilter("foo-request-filter-3", emptyRequestFilter);
    proxyServer.addRequestFilter("bar-request-filter-1", emptyRequestFilter);
    proxyServer.addRequestFilter("bar-request-filter-2", emptyRequestFilter);
    proxyServer.addRequestFilter("baz-request-filter-1", emptyRequestFilter);

    List<String> requestFilterNames = proxyServer.requestFilterNames();
    assertThat(requestFilterNames)
      .containsExactly(
        "foo-request-filter-1",
        "foo-request-filter-2",
        "foo-request-filter-3",
        "bar-request-filter-1",
        "bar-request-filter-2",
        "baz-request-filter-1"
      );

    requestFilterNames
      .stream()
      .filter(filterName -> filterName.startsWith("bar"))
      .forEach(proxyServer::removeRequestFilter);

    List<String> updatedRequestFilterNames = proxyServer.requestFilterNames();
    assertThat(updatedRequestFilterNames)
      .containsExactly(
        "foo-request-filter-1",
        "foo-request-filter-2",
        "foo-request-filter-3",
        "baz-request-filter-1"
      );
  }

  @Test
  void canGetRequestFilterByName() {
    proxyServer.start();

    RequestFilter emptyRequestFilter = (request, contents, messageInfo) -> null;

    proxyServer.addRequestFilter("foo-request-filter-1", emptyRequestFilter);
    proxyServer.addRequestFilter("foo-request-filter-2", emptyRequestFilter);
    proxyServer.addRequestFilter("foo-request-filter-3", emptyRequestFilter);
    proxyServer.addRequestFilter("bar-request-filter-1", emptyRequestFilter);
    proxyServer.addRequestFilter("bar-request-filter-2", emptyRequestFilter);
    proxyServer.addRequestFilter("baz-request-filter-2", emptyRequestFilter);

    RequestFilter requestFilter = proxyServer.requestFilter("foo-request-filter-2");

    assertThat(requestFilter).isEqualTo(emptyRequestFilter);
  }

  @Test
  void canGetResponseFilters() {
    proxyServer.start();

    proxyServer.responseFilterNames()
      .forEach(proxyServer::removeResponseFilter);

    ResponseFilter emptyResponseFilter = (response, contents, messageInfo) -> {
    };

    proxyServer.addResponseFilter("foo-response-filter-1", emptyResponseFilter);
    proxyServer.addResponseFilter("foo-response-filter-2", emptyResponseFilter);
    proxyServer.addResponseFilter("foo-response-filter-3", emptyResponseFilter);
    proxyServer.addResponseFilter("bar-response-filter-1", emptyResponseFilter);
    proxyServer.addResponseFilter("bar-response-filter-2", emptyResponseFilter);
    proxyServer.addResponseFilter("baz-response-filter-1", emptyResponseFilter);

    Map<String, ResponseFilter> responseFilters = proxyServer.responseFilters();
    assertThat(responseFilters)
      .hasSize(6)
      .isEqualTo(Map.of(
        "foo-response-filter-1", emptyResponseFilter,
        "foo-response-filter-2", emptyResponseFilter,
        "foo-response-filter-3", emptyResponseFilter,
        "bar-response-filter-1", emptyResponseFilter,
        "bar-response-filter-2", emptyResponseFilter,
        "baz-response-filter-1", emptyResponseFilter
      ));

    proxyServer.responseFilterNames()
      .stream()
      .filter(filterName -> filterName.startsWith("foo"))
      .forEach(proxyServer::removeResponseFilter);

    Map<String, ResponseFilter> updatedResponseFilters = proxyServer.responseFilters();
    assertThat(updatedResponseFilters)
      .hasSize(3)
      .isEqualTo(Map.of(
        "bar-response-filter-1", emptyResponseFilter,
        "bar-response-filter-2", emptyResponseFilter,
        "baz-response-filter-1", emptyResponseFilter
      ));
  }

  @Test
  void canGetResponseFilterNames() {
    proxyServer.start();

    proxyServer.responseFilterNames()
      .forEach(proxyServer::removeResponseFilter);

    ResponseFilter emptyResponseFilter = (response, contents, messageInfo) -> {
    };

    proxyServer.addResponseFilter("foo-response-filter-1", emptyResponseFilter);
    proxyServer.addResponseFilter("foo-response-filter-2", emptyResponseFilter);
    proxyServer.addResponseFilter("foo-response-filter-3", emptyResponseFilter);
    proxyServer.addResponseFilter("bar-response-filter-1", emptyResponseFilter);
    proxyServer.addResponseFilter("bar-response-filter-2", emptyResponseFilter);
    proxyServer.addResponseFilter("baz-response-filter-1", emptyResponseFilter);

    List<String> responseFilterNames = proxyServer.responseFilterNames();
    assertThat(responseFilterNames)
      .containsExactly(
        "foo-response-filter-1",
        "foo-response-filter-2",
        "foo-response-filter-3",
        "bar-response-filter-1",
        "bar-response-filter-2",
        "baz-response-filter-1"
      );

    responseFilterNames
      .stream()
      .filter(filterName -> filterName.startsWith("bar"))
      .forEach(proxyServer::removeResponseFilter);

    List<String> updatedResponseFilterNames = proxyServer.responseFilterNames();
    assertThat(updatedResponseFilterNames)
      .containsExactly(
        "foo-response-filter-1",
        "foo-response-filter-2",
        "foo-response-filter-3",
        "baz-response-filter-1"
      );
  }

  @Test
  void canGetResponseFilterByName() {
    proxyServer.start();

    ResponseFilter emptyResponseFilter = (response, contents, messageInfo) -> {
    };

    proxyServer.addResponseFilter("foo-response-filter-1", emptyResponseFilter);
    proxyServer.addResponseFilter("foo-response-filter-2", emptyResponseFilter);
    proxyServer.addResponseFilter("foo-response-filter-3", emptyResponseFilter);
    proxyServer.addResponseFilter("bar-response-filter-1", emptyResponseFilter);
    proxyServer.addResponseFilter("bar-response-filter-2", emptyResponseFilter);
    proxyServer.addResponseFilter("baz-response-filter-1", emptyResponseFilter);

    ResponseFilter responseFilter = proxyServer.responseFilter("foo-response-filter-3");
    assertThat(responseFilter).isEqualTo(emptyResponseFilter);
  }
}
