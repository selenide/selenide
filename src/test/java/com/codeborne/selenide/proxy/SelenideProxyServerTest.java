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

  private final RequestFilter emptyRequestFilter = (request, contents, messageInfo) -> null;
  private final ResponseFilter emptyResponseFilter = (response, contents, messageInfo) -> {
  };

  private final String[] selenideRequestFilter = new String[]{
    "selenide.proxy.filter.mockResponse",
    "selenide.proxy.filter.authentication",
    "selenide.proxy.filter.download"
  };

  private final String[] selenideResponseFilter = new String[]{
    "selenide.proxy.filter.download"
  };

  @Test
  void canInterceptResponses() {
    proxyServer.start();

    verify(bmp).setTrustAllServers(true);
    verify(bmp, never()).setChainedProxy(any(InetSocketAddress.class));
    verify(bmp).start(0);

    FileDownloadFilter filter = proxyServer.responseFilter("selenide.proxy.filter.download");
    assertThat(filter.downloads().files()).hasSize(0);

    FileDownloadFilter requestFilter = proxyServer.requestFilter("selenide.proxy.filter.download");
    assertThat(requestFilter).isSameAs(filter);
  }

  @Test
  void canChainProxyServersWithNoProxySettings() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("127.0.0.1:3128");
    proxy.setNoProxy("localhost,https://example.com/");

    SelenideProxyServer proxyServerWithChainedProxy = new SelenideProxyServer(config, proxy, bmp);
    proxyServerWithChainedProxy.start();

    verify(bmp).setChainedProxy(any(InetSocketAddress.class));
    verify(bmp).setChainedProxyNonProxyHosts(Arrays.asList("localhost", "https://example.com/"));
    verify(bmp).start(0);
  }

  @Test
  @SuppressWarnings("unchecked")
  void canChainProxyServersWithEmptyNoProxySettings() {
    Proxy proxy = new Proxy();
    proxy.setHttpProxy("127.0.0.1:3128");

    SelenideProxyServer proxyServerWithChainedProxy = new SelenideProxyServer(config, proxy, bmp);
    proxyServerWithChainedProxy.start();

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

    Map<String, RequestFilter> selenideRequestFilters = proxyServer.requestFilters();

    resetFilters();

    addRequestFilters(
      "foo-request-filter-1", "foo-request-filter-2", "foo-request-filter-3",
      "bar-request-filter-1", "bar-request-filter-2", "baz-request-filter-1"
    );

    Map<String, RequestFilter> requestFilters = proxyServer.requestFilters();
    assertThat(requestFilters)
      .hasSize(9)
      .containsAllEntriesOf(selenideRequestFilters)
      .containsAllEntriesOf(Map.of(
        "foo-request-filter-1", emptyRequestFilter,
        "foo-request-filter-2", emptyRequestFilter,
        "foo-request-filter-3", emptyRequestFilter,
        "bar-request-filter-1", emptyRequestFilter,
        "bar-request-filter-2", emptyRequestFilter,
        "baz-request-filter-1", emptyRequestFilter
      ));

    removeRequestFilters("foo-request-filter-1", "foo-request-filter-2", "foo-request-filter-3");

    Map<String, RequestFilter> updatedRequestFilters = proxyServer.requestFilters();
    assertThat(updatedRequestFilters)
      .hasSize(6)
      .containsAllEntriesOf(selenideRequestFilters)
      .containsAllEntriesOf(Map.of(
        "bar-request-filter-1", emptyRequestFilter,
        "bar-request-filter-2", emptyRequestFilter,
        "baz-request-filter-1", emptyRequestFilter
      ));
  }

  @Test
  void canGetRequestFilterNames() {
    proxyServer.start();
    resetFilters();

    addRequestFilters(
      "foo-request-filter-1", "foo-request-filter-2", "foo-request-filter-3",
      "bar-request-filter-1", "bar-request-filter-2", "baz-request-filter-1"
    );

    List<String> requestFilterNames = proxyServer.requestFilterNames();
    assertThat(requestFilterNames)
      .contains(selenideRequestFilter)
      .contains(
        "foo-request-filter-1",
        "foo-request-filter-2",
        "foo-request-filter-3",
        "bar-request-filter-1",
        "bar-request-filter-2",
        "baz-request-filter-1"
      );

    removeRequestFilters("bar-request-filter-1", "bar-request-filter-2");


    List<String> updatedRequestFilterNames = proxyServer.requestFilterNames();
    assertThat(updatedRequestFilterNames)
      .contains(selenideResponseFilter)
      .contains(
        "foo-request-filter-1",
        "foo-request-filter-2",
        "foo-request-filter-3",
        "baz-request-filter-1"
      );
  }

  @Test
  void canGetRequestFilterByName() {
    proxyServer.start();

    addRequestFilters(
      "foo-request-filter-1", "foo-request-filter-2", "foo-request-filter-3",
      "bar-request-filter-1", "bar-request-filter-2", "baz-request-filter-1"
    );

    RequestFilter requestFilter = proxyServer.requestFilter("foo-request-filter-2");

    assertThat(requestFilter).isEqualTo(emptyRequestFilter);
  }

  @Test
  void canGetResponseFilters() {
    proxyServer.start();

    Map<String, ResponseFilter> selenideResponseFilters = proxyServer.responseFilters();

    resetFilters();

    addResponseFilters(
      "foo-response-filter-1", "foo-response-filter-2", "foo-response-filter-3",
      "bar-response-filter-1", "bar-response-filter-2", "baz-response-filter-1"
    );

    Map<String, ResponseFilter> responseFilters = proxyServer.responseFilters();
    assertThat(responseFilters)
      .hasSize(7)
      .containsAllEntriesOf(selenideResponseFilters)
      .containsAllEntriesOf(Map.of(
        "foo-response-filter-1", emptyResponseFilter,
        "foo-response-filter-2", emptyResponseFilter,
        "foo-response-filter-3", emptyResponseFilter,
        "bar-response-filter-1", emptyResponseFilter,
        "bar-response-filter-2", emptyResponseFilter,
        "baz-response-filter-1", emptyResponseFilter
      ));

    removeResponseFilters("foo-response-filter-1", "foo-response-filter-2", "foo-response-filter-3");

    Map<String, ResponseFilter> updatedResponseFilters = proxyServer.responseFilters();
    assertThat(updatedResponseFilters)
      .hasSize(4)
      .containsAllEntriesOf(selenideResponseFilters)
      .containsAllEntriesOf(Map.of(
        "bar-response-filter-1", emptyResponseFilter,
        "bar-response-filter-2", emptyResponseFilter,
        "baz-response-filter-1", emptyResponseFilter
      ));
  }

  @Test
  void canGetResponseFilterNames() {
    proxyServer.start();
    resetFilters();

    addResponseFilters(
      "foo-response-filter-1", "foo-response-filter-2", "foo-response-filter-3",
      "bar-response-filter-1", "bar-response-filter-2", "baz-response-filter-1"
    );

    List<String> responseFilterNames = proxyServer.responseFilterNames();
    assertThat(responseFilterNames)
      .contains(selenideResponseFilter)
      .contains(
        "foo-response-filter-1",
        "foo-response-filter-2",
        "foo-response-filter-3",
        "bar-response-filter-1",
        "bar-response-filter-2",
        "baz-response-filter-1"
      );

    removeResponseFilters("bar-response-filter-1", "bar-response-filter-2");

    List<String> updatedResponseFilterNames = proxyServer.responseFilterNames();
    assertThat(updatedResponseFilterNames)
      .contains(selenideResponseFilter)
      .contains(
        "foo-response-filter-1",
        "foo-response-filter-2",
        "foo-response-filter-3",
        "baz-response-filter-1"
      );
  }

  @Test
  void canGetResponseFilterByName() {
    proxyServer.start();

    addResponseFilters(
      "foo-response-filter-1", "foo-response-filter-2", "foo-response-filter-3",
      "bar-response-filter-1", "bar-response-filter-2", "baz-response-filter-1"
    );

    ResponseFilter responseFilter = proxyServer.responseFilter("foo-response-filter-3");
    assertThat(responseFilter).isEqualTo(emptyResponseFilter);
  }

  @Test
  void canRemoveAllCustomFilters() {
    proxyServer.start();
    int initialRequestFilters = proxyServer.requestFilters().size();
    int initialResponseFilters = proxyServer.responseFilters().size();

    addRequestFilters("request-filter-1", "request-filter-2");
    addResponseFilters("response-filter-1", "response-filter-2", "response-filter-3");

    assertThat(proxyServer.requestFilters()).hasSize(initialRequestFilters + 2);
    assertThat(proxyServer.responseFilters()).hasSize(initialResponseFilters + 3);

    proxyServer.cleanupFilters();

    assertThat(proxyServer.requestFilters()).hasSize(initialRequestFilters);
    assertThat(proxyServer.responseFilters()).hasSize(initialResponseFilters);
  }

  @Test
  void canRemoveAllCustomRequestFilters() {
    proxyServer.start();
    int initialRequestFilters = proxyServer.requestFilters().size();
    int initialResponseFilters = proxyServer.responseFilters().size();

    addRequestFilters("request-filter-1", "request-filter-2");
    addResponseFilters("response-filter-1", "response-filter-2", "response-filter-3");

    assertThat(proxyServer.requestFilters()).hasSize(initialRequestFilters + 2);
    assertThat(proxyServer.responseFilters()).hasSize(initialResponseFilters + 3);

    proxyServer.cleanupRequestFilters();

    assertThat(proxyServer.requestFilters()).hasSize(initialRequestFilters);
    assertThat(proxyServer.responseFilters()).hasSize(initialResponseFilters + 3);
  }

  @Test
  void canRemoveAllCustomResponseFilters() {
    proxyServer.start();
    int initialRequestFilters = proxyServer.requestFilters().size();
    int initialResponseFilters = proxyServer.responseFilters().size();

    addRequestFilters("request-filter-1", "request-filter-2");
    addResponseFilters("response-filter-1", "response-filter-2", "response-filter-3");

    assertThat(proxyServer.requestFilters()).hasSize(initialRequestFilters + 2);
    assertThat(proxyServer.responseFilters()).hasSize(initialResponseFilters + 3);

    proxyServer.cleanupResponseFilters();

    assertThat(proxyServer.requestFilters()).hasSize(initialRequestFilters + 2);
    assertThat(proxyServer.responseFilters()).hasSize(initialResponseFilters);
  }

  @Test
  void unableRemoveSelenideRequestFilters() {
    proxyServer.start();
    int initialRequestFilters = proxyServer.requestFilters().size();
    int initialResponseFilters = proxyServer.responseFilters().size();

    assertThat(proxyServer.requestFilters()).allSatisfy((name, requestFilter) -> name.startsWith("selenide.proxy.filter."));
    assertThat(proxyServer.responseFilters()).allSatisfy((name, requestFilter) -> name.startsWith("selenide.proxy.filter."));

    RequestFilter requestFilter = proxyServer.requestFilter("selenide.proxy.filter.mockResponse");
    assertThat(requestFilter).isNotNull();

    proxyServer.removeRequestFilter("selenide.proxy.filter.mockResponse");

    assertThat(proxyServer.requestFilters()).hasSize(initialRequestFilters);
    assertThat(proxyServer.responseFilters()).hasSize(initialResponseFilters);
  }

  @Test
  void unableRemoveSelenideResponseFilters() {
    proxyServer.start();
    int initialRequestFilters = proxyServer.requestFilters().size();
    int initialResponseFilters = proxyServer.responseFilters().size();

    assertThat(proxyServer.requestFilters()).allSatisfy((name, requestFilter) -> name.startsWith("selenide.proxy.filter."));
    assertThat(proxyServer.responseFilters()).allSatisfy((name, requestFilter) -> name.startsWith("selenide.proxy.filter."));

    RequestFilter requestFilter = proxyServer.responseFilter("selenide.proxy.filter.download");
    assertThat(requestFilter).isNotNull();

    proxyServer.removeResponseFilter("selenide.proxy.filter.download");

    assertThat(proxyServer.requestFilters()).hasSize(initialRequestFilters);
    assertThat(proxyServer.responseFilters()).hasSize(initialResponseFilters);
  }

  private void addRequestFilters(String... names) {
    for (String name : names) {
      proxyServer.addRequestFilter(name, emptyRequestFilter);
    }
  }

  private void removeRequestFilters(String... names) {
    for (String name : names) {
      proxyServer.removeRequestFilter(name);
    }
  }

  private void addResponseFilters(String... names) {
    for (String name : names) {
      proxyServer.addResponseFilter(name, emptyResponseFilter);
    }
  }

  private void removeResponseFilters(String... names) {
    for (String name : names) {
      proxyServer.removeResponseFilter(name);
    }
  }

  private void resetFilters() {
    proxyServer.requestFilterNames()
      .forEach(proxyServer::removeRequestFilter);

    proxyServer.responseFilterNames()
      .forEach(proxyServer::removeResponseFilter);
  }
}
