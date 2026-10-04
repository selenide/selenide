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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.mcp.tools.HarEntryTestFactory.entry;
import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NetworkRequestsToolTest {
  private final BrowserUpProxy browserUpProxy = mock();
  private final SelenideDriver driver = mock();
  private final NetworkRequestsTool tool = new NetworkRequestsTool(session(driver));

  @Test
  void listsAllCapturedRequestsWhenNoPatternGiven() {
    withHar(entry("https://a.test/1", 200), entry("https://b.test/2", 404));

    McpSchema.CallToolResult result = tool.execute(Map.of());

    assertThat(text(result))
      .contains("GET https://a.test/1 -> 200")
      .contains("GET https://b.test/2 -> 404");
  }

  @Test
  void filtersByUrlPatternAcrossTheFullHistoryBeforeCappingTo100() {
    // The one matching entry is far older than the most recent 100 entries; a fix that
    // truncates to the last 100 raw entries *before* filtering would lose it.
    List<HarEntry> entries = new ArrayList<>();
    entries.add(entry("https://match.test/keep", 200));
    for (int i = 0; i < 149; i++) {
      entries.add(entry("https://noise.test/" + i, 200));
    }
    withHar(entries.toArray(new HarEntry[0]));

    McpSchema.CallToolResult result = tool.execute(Map.of("urlPattern", "match.test"));

    assertThat(text(result)).contains("https://match.test/keep");
  }

  @Test
  void reportsNoMatchesRatherThanThrowing() {
    withHar();

    McpSchema.CallToolResult result = tool.execute(Map.of());

    assertThat(text(result)).isEqualTo("No matching network requests");
  }

  @Test
  void translatesProxyNotEnabledIntoAFriendlyMessage() {
    when(driver.getProxy()).thenThrow(new IllegalStateException("Proxy server is not enabled."));

    assertThatThrownBy(() -> tool.execute(Map.of()))
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
