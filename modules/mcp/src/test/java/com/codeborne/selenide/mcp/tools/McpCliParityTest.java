package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.cli.SelenideCli;
import com.codeborne.selenide.mcp.BrowserSession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Keeps the MCP and CLI front-ends aligned on the shared set of browser actions.
 *
 * <p>Both modules wrap the same Selenide actions but expose them differently (the CLI as commands,
 * MCP as tools) and are free to add extras — MCP is richer for agent introspection (snapshot,
 * network, tabs), the CLI adds scripting conveniences (checkbox/radio, key presses). {@link #CATALOG}
 * is the explicit contract of actions that <em>both</em> must support. Adding a shared capability
 * means adding a row here, which fails this test until each side implements its half — that is the
 * forcing function that stops the two front-ends from drifting apart.
 *
 * <p>Extras on either side are intentionally not asserted here.
 */
class McpCliParityTest {
  /**
   * One shared browser action: the CLI command name and the MCP tool name that must both exist.
   */
  private record SharedAction(String cliCommand, String mcpTool) {
  }

  private static final List<SharedAction> CATALOG = List.of(
    new SharedAction("open", "browser_navigate"),
    new SharedAction("click", "browser_click"),
    new SharedAction("setvalue", "browser_set_value"),
    new SharedAction("type", "browser_type"),
    new SharedAction("clear", "browser_clear"),
    new SharedAction("hover", "browser_hover"),
    new SharedAction("selectoption", "browser_select_option"),
    // CLI splits key presses into pressEnter/pressTab/pressEscape; MCP has a generic browser_press_key.
    new SharedAction("pressenter", "browser_press_key"),
    new SharedAction("back", "browser_back"),
    new SharedAction("forward", "browser_forward"),
    new SharedAction("refresh", "browser_refresh"),
    new SharedAction("screenshot", "browser_screenshot"));

  @Test
  void everySharedActionIsSupportedByBothFrontEnds() {
    Set<String> cliCommands = SelenideCli.commandNames();
    Set<String> mcpTools = registeredMcpToolNames();

    assertThat(CATALOG)
      .allSatisfy(action -> {
        assertThat(cliCommands)
          .as("CLI is missing shared command '%s'", action.cliCommand())
          .contains(action.cliCommand());
        assertThat(mcpTools)
          .as("MCP is missing shared tool '%s'", action.mcpTool())
          .contains(action.mcpTool());
      });
  }

  private static Set<String> registeredMcpToolNames() {
    BrowserSession session = mock(BrowserSession.class);
    return Stream.of(
        NavigationTools.specs(session),
        ElementInteractionTools.specs(session),
        AdvancedInteractionTools.specs(session),
        InspectTools.specs(session),
        NetworkTools.specs(session),
        CodegenTools.specs(session))
      .flatMap(List::stream)
      .map(spec -> spec.tool().name())
      .collect(toSet());
  }
}
