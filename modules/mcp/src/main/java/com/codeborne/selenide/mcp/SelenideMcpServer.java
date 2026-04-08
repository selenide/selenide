package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.mcp.tools.AdvancedInteractionTools;
import com.codeborne.selenide.mcp.tools.CodegenTools;
import com.codeborne.selenide.mcp.tools.ElementInteractionTools;
import com.codeborne.selenide.mcp.tools.InspectTools;
import com.codeborne.selenide.mcp.tools.NavigationTools;
import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class SelenideMcpServer {
  private final BrowserSession session;

  public SelenideMcpServer(SelenideConfig config) {
    this.session = new BrowserSession(config);
  }

  @SuppressWarnings("unchecked") // MCP SDK's tools() uses generic varargs
  public void start(String[] args) {
    StdioServerTransportProvider transport = new StdioServerTransportProvider(McpJsonDefaults.getMapper());

    var builder = McpServer.sync(transport)
      .serverInfo("selenide-mcp", getVersion())
      .tools(NavigationTools.specs(session))
      .tools(ElementInteractionTools.specs(session))
      .tools(AdvancedInteractionTools.specs(session))
      .tools(InspectTools.specs(session));

    if (hasCapability(args, "codegen")) {
      builder = builder.tools(CodegenTools.specs(session));
    }

    McpSyncServer server = builder.build();

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
    server.start(args);
  }

  static boolean hasCapability(String[] args, String capability) {
    for (String arg : args) {
      if (arg.startsWith("--caps=")) {
        String[] caps = arg.substring("--caps=".length()).split(",");
        if (Arrays.asList(caps).contains(capability)) {
          return true;
        }
      }
    }
    return false;
  }

  private static String getVersion() {
    String version = SelenideMcpServer.class.getPackage().getImplementationVersion();
    return version != null ? version : "dev";
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
