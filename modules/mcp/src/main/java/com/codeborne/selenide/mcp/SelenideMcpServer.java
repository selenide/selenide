package com.codeborne.selenide.mcp;

import com.codeborne.selenide.AssertionMode;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelectorMode;
import com.codeborne.selenide.TextCheck;
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

/**
 * MCP (Model Context Protocol) server that exposes Selenide browser automation
 * as tools consumable by AI assistants such as Claude.
 *
 * <p>Start via {@link #main(String[])} passing CLI arguments to configure the browser session.
 * See the module README for the full list of supported parameters.
 */
public class SelenideMcpServer {
  private final BrowserSession session;

  /**
   * Creates a server backed by a browser session configured with the given {@code config}.
   */
  public SelenideMcpServer(SelenideConfig config) {
    this.session = new BrowserSession(config);
  }

  /**
   * Starts the MCP server over stdio and blocks until the JVM shuts down.
   *
   * @param args command-line arguments; {@code --caps=codegen} enables the codegen toolset
   */
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
      applyBrowserArg(config, arg);
      applyConnectionArg(config, arg);
      applyProxyArg(config, arg);
      applyBehaviorArg(config, arg);
      applyModeArg(config, arg);
    }
    return config;
  }

  private static void applyBrowserArg(SelenideConfig config, String arg) {
    if (arg.startsWith("--browser=")) {
      config.browser(arg.substring("--browser=".length()));
    }
    else if (arg.startsWith("--browser-version=")) {
      config.browserVersion(arg.substring("--browser-version=".length()));
    }
    else if (arg.startsWith("--browser-size=")) {
      config.browserSize(arg.substring("--browser-size=".length()));
    }
    else if (arg.startsWith("--browser-binary=")) {
      config.browserBinary(arg.substring("--browser-binary=".length()));
    }
    else if (arg.startsWith("--browser-position=")) {
      config.browserPosition(arg.substring("--browser-position=".length()));
    }
    else if (arg.equals("--headless")) {
      config.headless(true);
    }
  }

  private static void applyConnectionArg(SelenideConfig config, String arg) {
    if (arg.startsWith("--base-url=")) {
      config.baseUrl(arg.substring("--base-url=".length()));
    }
    else if (arg.startsWith("--timeout=")) {
      config.timeout(Long.parseLong(arg.substring("--timeout=".length())));
    }
    else if (arg.startsWith("--polling-interval=")) {
      config.pollingInterval(Long.parseLong(arg.substring("--polling-interval=".length())));
    }
    else if (arg.startsWith("--remote=")) {
      config.remote(arg.substring("--remote=".length()));
    }
    else if (arg.startsWith("--remote-read-timeout=")) {
      config.remoteReadTimeout(Long.parseLong(arg.substring("--remote-read-timeout=".length())));
    }
    else if (arg.startsWith("--remote-connection-timeout=")) {
      config.remoteConnectionTimeout(Long.parseLong(arg.substring("--remote-connection-timeout=".length())));
    }
    else if (arg.startsWith("--page-load-strategy=")) {
      config.pageLoadStrategy(arg.substring("--page-load-strategy=".length()));
    }
    else if (arg.startsWith("--page-load-timeout=")) {
      config.pageLoadTimeout(Long.parseLong(arg.substring("--page-load-timeout=".length())));
    }
    else if (arg.startsWith("--downloads-folder=")) {
      config.downloadsFolder(arg.substring("--downloads-folder=".length()));
    }
  }

  private static void applyProxyArg(SelenideConfig config, String arg) {
    if (arg.equals("--proxy-enabled")) {
      config.proxyEnabled(true);
    }
    else if (arg.startsWith("--proxy-host=")) {
      config.proxyHost(arg.substring("--proxy-host=".length()));
    }
    else if (arg.startsWith("--proxy-port=")) {
      config.proxyPort(Integer.parseInt(arg.substring("--proxy-port=".length())));
    }
  }

  private static void applyBehaviorArg(SelenideConfig config, String arg) {
    if (arg.equals("--fast-set-value")) {
      config.fastSetValue(true);
    }
    else if (arg.equals("--click-via-js")) {
      config.clickViaJs(true);
    }
    else if (arg.equals("--webdriver-logs")) {
      config.webdriverLogsEnabled(true);
    }
    else if (arg.equals("--no-screenshots")) {
      config.screenshots(false);
    }
    else if (arg.equals("--no-save-page-source")) {
      config.savePageSource(false);
    }
    else if (arg.equals("--no-reopen-browser-on-fail")) {
      config.reopenBrowserOnFail(false);
    }
    else if (arg.startsWith("--reports-folder=")) {
      config.reportsFolder(arg.substring("--reports-folder=".length()));
    }
  }

  private static void applyModeArg(SelenideConfig config, String arg) {
    if (arg.startsWith("--text-check=")) {
      config.textCheck(TextCheck.valueOf(arg.substring("--text-check=".length())));
    }
    else if (arg.startsWith("--selector-mode=")) {
      config.selectorMode(SelectorMode.valueOf(arg.substring("--selector-mode=".length())));
    }
    else if (arg.startsWith("--assertion-mode=")) {
      config.assertionMode(AssertionMode.valueOf(arg.substring("--assertion-mode=".length())));
    }
    else if (arg.startsWith("--file-download=")) {
      config.fileDownload(FileDownloadMode.valueOf(arg.substring("--file-download=".length())));
    }
  }
}
