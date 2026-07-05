package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class TabCloseToolTest {
  private final WebDriver driver = mock(withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
  private final TabCloseTool tool = new TabCloseTool(session(driver));

  @Test
  void closingTheOnlyRemainingTabDoesNotProbeHandlesAfterClose() {
    when(driver.getWindowHandle()).thenReturn("h1");
    when(driver.getWindowHandles())
      .thenReturn(Set.of("h1"))
      .thenThrow(new NoSuchSessionException("session already ended"));

    McpSchema.CallToolResult result = tool.execute(Map.of());

    assertThat(text(result)).isEqualTo("Closed last tab; no remaining tabs");
    verify(driver, times(1)).getWindowHandles();
    verify(driver).close();
  }

  @Test
  void closingTheActiveTabSwitchesToARemainingOne() {
    when(driver.getWindowHandle()).thenReturn("h1");
    when(driver.getWindowHandles())
      .thenReturn(new LinkedHashSet<>(List.of("h1", "h2")))
      .thenReturn(Set.of("h2"));

    McpSchema.CallToolResult result = tool.execute(Map.of());

    verify(driver.switchTo()).window("h2");
    assertThat(text(result)).isEqualTo("Closed tab: h1");
  }

  @Test
  void closingANonActiveTabByIndexKeepsTheActiveOneFocused() {
    when(driver.getWindowHandle()).thenReturn("active");
    Set<String> beforeClose = new LinkedHashSet<>(List.of("active", "other"));
    when(driver.getWindowHandles())
      .thenReturn(beforeClose, beforeClose, Set.of("active"));

    McpSchema.CallToolResult result = tool.execute(Map.of("index", 1));

    verify(driver.switchTo()).window("other");
    verify(driver.switchTo()).window("active");
    assertThat(text(result)).isEqualTo("Closed tab: other");
  }

  @Test
  void rejectsOutOfRangeIndex() {
    when(driver.getWindowHandles()).thenReturn(Set.of("h1"));

    assertThatThrownBy(() -> tool.execute(Map.of("index", 3)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Tab index 3 out of range");
  }

  @Test
  void rejectsBothIndexAndHandle() {
    assertThatThrownBy(() -> tool.execute(Map.of("index", 0, "handle", "h1")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Provide only one of 'index' or 'handle'");
  }

  static BrowserSession session(WebDriver webDriver) {
    BrowserSession session = mock();
    SelenideDriver driver = mock();
    when(session.getDriver()).thenReturn(driver);
    when(driver.getWebDriver()).thenReturn(webDriver);
    return session;
  }
}
