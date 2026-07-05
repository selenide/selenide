package com.codeborne.selenide.mcp.tools;

import com.browserup.bup.BrowserUpProxy;
import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import de.sstoehr.harreader.model.Har;
import de.sstoehr.harreader.model.HarEntry;
import io.modelcontextprotocol.spec.McpSchema;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
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
    SelenideProxyServer proxy = NetworkTools.requireProxy(session);
    String pattern = (String) args.get("urlPattern");
    List<HarEntry> matching = matchingEntries(harEntries(proxy.getProxy()), pattern);
    if (matching.isEmpty()) {
      return success("No matching network requests");
    }
    StringBuilder out = new StringBuilder();
    int start = Math.max(0, matching.size() - MAX_ENTRIES);
    for (int i = start; i < matching.size(); i++) {
      HarEntry e = matching.get(i);
      out.append(nullToDash(e.getRequest().getMethod())).append(' ').append(e.getRequest().getUrl())
        .append(" -> ").append(e.getResponse().getStatus()).append(' ')
        .append(nullToDash(e.getResponse().getContent().getMimeType())).append(' ')
        .append(e.getTime() != null ? (long) e.getTime() : 0L).append("ms\n");
    }
    return success(out.toString().trim());
  }

  private static List<HarEntry> matchingEntries(List<HarEntry> entries, @Nullable String pattern) {
    List<HarEntry> matching = new ArrayList<>();
    for (HarEntry e : entries) {
      String url = e.getRequest().getUrl();
      if (url == null) continue;
      if (pattern != null && !url.contains(pattern)) continue;
      matching.add(e);
    }
    return matching;
  }

  static List<HarEntry> harEntries(BrowserUpProxy proxy) {
    Har har = proxy.getHar();
    if (har == null) {
      proxy.newHar("selenide-mcp");
      return List.of();
    }
    return har.getLog().getEntries();
  }

  static String nullToDash(@Nullable Object o) {
    if (o == null) return "-";
    String s = o.toString();
    return s.isEmpty() ? "-" : s;
  }
}
