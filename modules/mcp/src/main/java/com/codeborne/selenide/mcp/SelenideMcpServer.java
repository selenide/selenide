package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.mcp.tools.AdvancedInteractionTools;
import com.codeborne.selenide.mcp.tools.ElementInteractionTools;
import com.codeborne.selenide.mcp.tools.NavigationTools;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

import java.util.concurrent.CountDownLatch;

public class SelenideMcpServer {
  private final BrowserSession session;

  public SelenideMcpServer(SelenideConfig config) {
    this.session = new BrowserSession(config);
  }

  public void start() {
    StdioServerTransportProvider transport = new StdioServerTransportProvider(McpJsonDefaults.getMapper());

    McpSyncServer server = McpServer.sync(transport)
      .serverInfo("selenide-mcp", "1.0.0")
      .tools(NavigationTools.specs(session))
      .tools(ElementInteractionTools.specs(session))
      .tools(AdvancedInteractionTools.specs(session))
      .build();

    CountDownLatch latch = new CountDownLatch(1);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      session.close();
      server.closeGracefully();
      latch.countDown();
    }));

    try {
      latch.await();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static void main(String[] args) {
    SelenideConfig config = parseConfig(args);
    SelenideMcpServer server = new SelenideMcpServer(config);
    server.start();
  }

  static SelenideConfig parseConfig(String[] args) {
    SelenideConfig config = new SelenideConfig();
    for (String arg : args) {
      if (arg.startsWith("--browser=")) {
        config.browser(arg.substring("--browser=".length()));
      }
      else if (arg.equals("--headless")) {
        config.headless(true);
      }
      else if (arg.startsWith("--base-url=")) {
        config.baseUrl(arg.substring("--base-url=".length()));
      }
      else if (arg.startsWith("--timeout=")) {
        config.timeout(Long.parseLong(arg.substring("--timeout=".length())));
      }
    }
    return config;
  }
}
