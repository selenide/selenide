package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.RequestFilters;
import com.codeborne.selenide.ResponseFilters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static org.assertj.core.api.Assertions.assertThat;

final class ProxyFiltersFromConfigurationTest extends ProxyIntegrationTest {
  @BeforeEach
  void setUp() {
    Configuration.requestFilters = RequestFilters.from("foo-filter.request", (request, contents, messageInfo) -> null);
    Configuration.responseFilters = ResponseFilters.from("bar-filter.response", (response, contents, messageInfo) -> {
    });

    openFile("page_with_uploads.html");
  }

  @Test
  public void canConfigureProxyFilters() {
    List<String> requestFilterNames = getSelenideProxy().requestFilterNames();
    assertThat(requestFilterNames).contains("config.proxy.filter.foo-filter.request");

    List<String> responseFilterNames = getSelenideProxy().responseFilterNames();
    assertThat(responseFilterNames).contains("config.proxy.filter.bar-filter.response");
  }
}
