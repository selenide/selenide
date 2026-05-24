package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import de.sstoehr.harreader.model.HarEntry;
import de.sstoehr.harreader.model.HarHeader;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

class NetworkRequestTool extends McpTool {
  NetworkRequestTool(BrowserSession session) {
    super(session, "browser_network_request",
      "Get full detail (headers, status, duration) for the most recent network "
        + "request whose URL contains 'urlPattern'. Requires --proxy-enabled.");
  }

  @Override
  String inputSchema() {
    return """
      {
        "type": "object",
        "properties": {
          "urlPattern": {"type": "string", "description": "Substring to match against the URL"}
        },
        "required": ["urlPattern"]
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
    if (pattern == null || pattern.isEmpty()) {
      throw new IllegalArgumentException("'urlPattern' is required");
    }
    List<HarEntry> entries = NetworkRequestsTool.harEntries(proxy.getProxy());
    HarEntry match = null;
    for (int i = entries.size() - 1; i >= 0; i--) {
      if (entries.get(i).getRequest().getUrl().contains(pattern)) {
        match = entries.get(i);
        break;
      }
    }
    if (match == null) {
      throw new IllegalArgumentException("No network request matching: " + pattern);
    }
    return success(format(match));
  }

  private static String format(HarEntry e) {
    StringBuilder out = new StringBuilder();
    out.append(e.getRequest().getMethod()).append(' ')
      .append(e.getRequest().getUrl()).append('\n');
    out.append("Status: ").append(e.getResponse().getStatus()).append(' ')
      .append(e.getResponse().getStatusText()).append('\n');
    out.append("Duration: ").append((long) e.getTime()).append("ms\n");
    out.append("Request headers:\n");
    appendHeaders(out, e.getRequest().getHeaders());
    out.append("Response headers:\n");
    appendHeaders(out, e.getResponse().getHeaders());
    return out.toString().trim();
  }

  private static void appendHeaders(StringBuilder out, List<HarHeader> headers) {
    for (HarHeader h : headers) {
      out.append("  ").append(h.getName()).append(": ").append(h.getValue()).append('\n');
    }
  }
}
