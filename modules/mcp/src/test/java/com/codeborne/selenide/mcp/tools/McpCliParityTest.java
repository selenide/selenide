package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.cli.SelenideCli;
import com.codeborne.selenide.mcp.BrowserSession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Keeps the MCP and CLI front-ends aligned on the shared set of browser actions.
 *
 * <p>Both modules wrap the same Selenide actions but expose them differently (the CLI as commands,
 * MCP as tools). By convention, a CLI command {@code fooBar} has a matching MCP tool
 * {@code browser_foo_bar} — every command/tool on both sides is checked against that convention, so
 * a newly added action can't silently drift out of sync between the two front-ends the way {@code
 * append} once did (it existed in the CLI for a long time before an equivalent {@code browser_append}
 * MCP tool was added).
 *
 * <p>Genuine one-sided or renamed actions are not bugs — the CLI has scripting-only conveniences
 * (checkbox/radio, key presses), and MCP is richer for agent introspection (snapshot, network, tabs).
 * Those must be registered explicitly below ({@link #RENAMED_ACTIONS}, {@link #CLI_ONLY}, {@link
 * #MCP_ONLY}); anything left unregistered is treated as an unintentional asymmetry and fails the test.
 */
class McpCliParityTest {

  /**
   * CLI command -> MCP tool, for pairs whose names don't follow the standard naming convention —
   * including several CLI commands that fold into one richer MCP tool.
   */
  private static final Map<String, String> RENAMED_ACTIONS = Map.of(
    "open", "browser_navigate",
    "frame", "browser_frame_select",
    "defaultcontent", "browser_frame_reset",
    "pressenter", "browser_press_key",
    "presstab", "browser_press_key",
    "pressescape", "browser_press_key"
  );

  /** CLI commands that are scripting-only conveniences with no agent-facing MCP equivalent. */
  private static final Set<String> CLI_ONLY = Set.of(
    "should", "doubleclick", "contextclick", "scrollto", "selectradio", "setselected", "check", "uncheck"
  );

  /** MCP tools for agent introspection/automation with no CLI equivalent. */
  private static final Set<String> MCP_ONLY = Set.of(
    "browser_close", "browser_console_logs", "browser_drag_and_drop", "browser_execute_js",
    "browser_fill_form", "browser_find", "browser_find_all", "browser_generate_locator",
    "browser_generate_page_object", "browser_get_text", "browser_get_url", "browser_handle_dialog",
    "browser_network_request", "browser_network_requests", "browser_resize", "browser_snapshot",
    "browser_tab_close", "browser_tab_list", "browser_tab_new", "browser_tab_select",
    "browser_upload_file", "browser_wait_for", "selenide_docs"
  );

  @Test
  void everyCliCommandHasAMatchingMcpTool() {
    Set<String> mcpTools = registeredMcpToolNames();

    for (String cli : SelenideCli.commandNames()) {
      if (CLI_ONLY.contains(cli)) continue;

      String expectedTool = RENAMED_ACTIONS.getOrDefault(cli, "browser_" + cli);
      assertThat(mcpTools)
        .as("CLI command '%s' has no matching MCP tool (expected '%s' by naming convention). " +
          "If this asymmetry is intentional, register it in RENAMED_ACTIONS or CLI_ONLY.", cli, expectedTool)
        .anyMatch(tool -> normalize(tool).equals(normalize(expectedTool)));
    }
  }

  @Test
  void everyMcpToolHasAMatchingCliCommand() {
    Set<String> cliCommands = SelenideCli.commandNames();
    Set<String> renameTargets = Set.copyOf(RENAMED_ACTIONS.values());

    for (String tool : registeredMcpToolNames()) {
      if (MCP_ONLY.contains(tool) || renameTargets.contains(tool)) continue;

      assertThat(cliCommands)
        .as("MCP tool '%s' has no matching CLI command by naming convention. " +
          "If this asymmetry is intentional, register it in MCP_ONLY.", tool)
        .anyMatch(cli -> normalize(cli).equals(normalize(tool)));
    }
  }

  private static String normalize(String name) {
    return name.replace("browser_", "").replace("_", "");
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
