package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TabSelectToolTest {
  private final WebDriver driver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
  private final TabSelectTool tool = new TabSelectTool(session(driver));

  @Test
  void switchesByIndex() {
    when(driver.getWindowHandles()).thenReturn(new LinkedHashSet<>(List.of("h1", "h2")));

    McpSchema.CallToolResult result = tool.execute(Map.of("index", 1));

    verify(driver.switchTo()).window("h2");
    assertThat(text(result)).isEqualTo("Switched to tab: h2");
  }

  @Test
  void switchesByHandle() {
    McpSchema.CallToolResult result = tool.execute(Map.of("handle", "h9"));

    verify(driver.switchTo()).window("h9");
    assertThat(text(result)).isEqualTo("Switched to tab: h9");
  }

  @Test
  void rejectsNeitherIndexNorHandle() {
    assertThatThrownBy(() -> tool.execute(Map.of()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Provide either 'index' or 'handle'");
  }

  @Test
  void rejectsBothIndexAndHandle() {
    assertThatThrownBy(() -> tool.execute(Map.of("index", 0, "handle", "h1")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Provide only one of 'index' or 'handle'");
  }

  @Test
  void rejectsOutOfRangeIndex() {
    when(driver.getWindowHandles()).thenReturn(new LinkedHashSet<>(List.of("h1", "h2")));

    assertThatThrownBy(() -> tool.execute(Map.of("index", 5)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Tab index 5 out of range (0..1)");
  }

  static BrowserSession session(WebDriver webDriver) {
    BrowserSession session = mock(BrowserSession.class);
    SelenideDriver driver = mock(SelenideDriver.class);
    when(session.getDriver()).thenReturn(driver);
    when(driver.getWebDriver()).thenReturn(webDriver);
    return session;
  }
}
