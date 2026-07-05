package com.codeborne.selenide.mcp.tools;

import com.browserup.bup.BrowserUpProxy;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import de.sstoehr.harreader.model.Har;
import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarLog;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.mcp.tools.HarEntryTestFactory.entry;
import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NetworkRequestToolTest {
  private final BrowserUpProxy browserUpProxy = mock();
  private final SelenideDriver driver = mock();
  private final NetworkRequestTool tool = new NetworkRequestTool(session(driver));

  @Test
  void returnsTheMostRecentMatchingRequest() {
    withHar(
      entry("https://a.test/login", 200),
      entry("https://a.test/login", 500)
    );

    McpSchema.CallToolResult result = tool.execute(Map.of("urlPattern", "login"));

    assertThat(text(result))
      .contains("GET https://a.test/login")
      .contains("Status: 500");
  }

  @Test
  void rejectsMissingUrlPattern() {
    assertThatThrownBy(() -> tool.execute(Map.of()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("'urlPattern' is required");
  }

  @Test
  void throwsWhenNothingMatches() {
    withHar(entry("https://a.test/other", 200));

    assertThatThrownBy(() -> tool.execute(Map.of("urlPattern", "login")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("No network request matching: login");
  }

  @Test
  void translatesProxyNotEnabledIntoAFriendlyMessage() {
    when(driver.getProxy()).thenThrow(new IllegalStateException("Proxy server is not enabled."));

    assertThatThrownBy(() -> tool.execute(Map.of("urlPattern", "login")))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("Network capture requires --proxy-enabled at server startup");
  }

  private void withHar(HarEntry... entries) {
    SelenideProxyServer proxy = mock();
    when(driver.getProxy()).thenReturn(proxy);
    when(proxy.getProxy()).thenReturn(browserUpProxy);
    Har har = new Har();
    HarLog log = new HarLog();
    log.setEntries(List.of(entries));
    har.setLog(log);
    when(browserUpProxy.getHar()).thenReturn(har);
  }

  static BrowserSession session(SelenideDriver driver) {
    BrowserSession session = mock();
    when(session.getDriver()).thenReturn(driver);
    return session;
  }
}
