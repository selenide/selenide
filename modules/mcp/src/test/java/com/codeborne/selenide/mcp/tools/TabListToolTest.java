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
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TabListToolTest {
  private final WebDriver driver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
  private final TabListTool tool = new TabListTool(session(driver));

  @Test
  void listsAllTabsAndMarksTheActiveOne() {
    when(driver.getWindowHandle()).thenReturn("h1");
    when(driver.getWindowHandles()).thenReturn(new LinkedHashSet<>(List.of("h1", "h2")));
    when(driver.getTitle()).thenReturn("Page One", "Page Two");
    when(driver.getCurrentUrl()).thenReturn("https://a.test", "https://b.test");

    McpSchema.CallToolResult result = tool.execute(Map.of());

    assertThat(text(result))
      .contains("[0] handle=\"h1\" title=\"Page One\" url=\"https://a.test\" (active)")
      .contains("[1] handle=\"h2\" title=\"Page Two\" url=\"https://b.test\"")
      .doesNotContain("[1] handle=\"h2\" title=\"Page Two\" url=\"https://b.test\" (active)");
  }

  static BrowserSession session(WebDriver webDriver) {
    BrowserSession session = mock(BrowserSession.class);
    SelenideDriver driver = mock(SelenideDriver.class);
    when(session.getDriver()).thenReturn(driver);
    when(driver.getWebDriver()).thenReturn(webDriver);
    return session;
  }
}
