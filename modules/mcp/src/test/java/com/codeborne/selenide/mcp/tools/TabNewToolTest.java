package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.Map;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TabNewToolTest {
  private final WebDriver webDriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
  private final SelenideDriver driver = mock(SelenideDriver.class);
  private final TabNewTool tool = new TabNewTool(session(driver, webDriver));

  @Test
  void opensNewTabWithoutNavigating() {
    McpSchema.CallToolResult result = tool.execute(Map.of());

    verify(webDriver.switchTo()).newWindow(WindowType.TAB);
    verify(driver, never()).open(anyString());
    assertThat(text(result)).isEqualTo("Opened new tab");
  }

  @Test
  void opensNewTabAndNavigatesToUrl() {
    McpSchema.CallToolResult result = tool.execute(Map.of("url", "https://example.test"));

    verify(webDriver.switchTo()).newWindow(WindowType.TAB);
    verify(driver).open("https://example.test");
    assertThat(text(result)).isEqualTo("Opened new tab at https://example.test");
  }

  static BrowserSession session(SelenideDriver driver, WebDriver webDriver) {
    BrowserSession session = mock(BrowserSession.class);
    when(session.getDriver()).thenReturn(driver);
    when(driver.getWebDriver()).thenReturn(webDriver);
    return session;
  }
}
