package com.codeborne.selenide.mcp.tools;

import com.browserup.bup.BrowserUpProxy;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import de.sstoehr.harreader.model.Har;
import de.sstoehr.harreader.model.HarEntry;
import io.modelcontextprotocol.spec.McpSchema;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

class NetworkRequestsTool extends McpTool {
  private static final int MAX_ENTRIES = 100;

  NetworkRequestsTool(BrowserSession session) {
    super(session, "browser_network_requests",
      "List recently captured network requests. Requires --proxy-enabled at startup. "
        + "Optional 'urlPattern' filters by URL substring.");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "urlPattern": {"type": "string", "description": "Substring to match against the URL"}
        }
      }
      """;
  }

  @Override
  McpSchema.CallToolResult execute(Map<String, Object> args) {
    SelenideProxyServer proxy = session.getDriver().getProxy();
    if (proxy == null) {
      throw new IllegalStateException(
        "Network capture requires --proxy-enabled at server startup");
    }
    String pattern = (String) args.get("urlPattern");
    List<HarEntry> entries = harEntries(proxy.getProxy());
    StringBuilder out = new StringBuilder();
    int start = Math.max(0, entries.size() - MAX_ENTRIES);
    int shown = 0;
    for (int i = start; i < entries.size(); i++) {
      HarEntry e = entries.get(i);
      String url = e.getRequest().getUrl();
      if (pattern != null && !url.contains(pattern)) continue;
      out.append(e.getRequest().getMethod()).append(' ').append(url)
        .append(" -> ").append(e.getResponse().getStatus()).append(' ')
        .append(nullToDash(e.getResponse().getContent().getMimeType())).append(' ')
        .append((long) e.getTime()).append("ms\n");
      shown++;
    }
    if (shown == 0) {
      return success("No matching network requests");
    }
    return success(out.toString().trim());
  }

  static List<HarEntry> harEntries(BrowserUpProxy proxy) {
    Har har = proxy.getHar();
    if (har == null) {
      proxy.newHar("selenide-mcp");
      return List.of();
    }
    return har.getLog().getEntries();
  }

  private static String nullToDash(@Nullable String s) {
    return s == null || s.isEmpty() ? "-" : s;
  }
}
