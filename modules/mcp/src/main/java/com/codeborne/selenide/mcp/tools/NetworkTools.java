package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.mcp.BrowserSession;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import io.modelcontextprotocol.server.McpServerFeatures;

import java.util.List;

public class NetworkTools {
  private NetworkTools() {
  }

  public static List<McpServerFeatures.SyncToolSpecification> specs(BrowserSession session) {
    return List.of(
      new NetworkRequestsTool(session).spec(),
      new NetworkRequestTool(session).spec()
    );
  }

  /**
   * {@code session.getDriver().getProxy()} never returns null: it throws its own
   * {@code IllegalStateException} when the proxy isn't enabled. Translate that into
   * the message these tools actually document.
   */
  static SelenideProxyServer requireProxy(BrowserSession session) {
    try {
      return session.getDriver().getProxy();
    }
    catch (IllegalStateException e) {
      throw new IllegalStateException("Network capture requires --proxy-enabled at server startup", e);
    }
  }
}
