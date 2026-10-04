package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import java.util.Map;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class ResizeToolTest {
  private final WebDriver webDriver = mock(withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
  private final ResizeTool tool = new ResizeTool(session(webDriver));

  @Test
  void resizesToGivenDimensions() {
    McpSchema.CallToolResult result = tool.execute(Map.of("width", 1024, "height", 768));

    verify(webDriver.manage().window()).setSize(new Dimension(1024, 768));
    assertThat(text(result)).isEqualTo("Resized to 1024x768");
  }

  @Test
  void rejectsMissingWidth() {
    assertThatThrownBy(() -> tool.execute(Map.of("height", 768)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("'width' and 'height' are required");
  }

  @Test
  void rejectsMissingHeight() {
    assertThatThrownBy(() -> tool.execute(Map.of("width", 1024)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("'width' and 'height' are required");
  }

  static BrowserSession session(WebDriver webDriver) {
    BrowserSession session = mock();
    SelenideDriver driver = mock();
    when(session.getDriver()).thenReturn(driver);
    when(driver.getWebDriver()).thenReturn(webDriver);
    return session;
  }
}
