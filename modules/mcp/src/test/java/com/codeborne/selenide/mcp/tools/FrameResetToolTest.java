package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideTargetLocator;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FrameResetToolTest {
  private final SelenideDriver driver = mock();
  private final SelenideTargetLocator targetLocator = mock();
  private final FrameResetTool tool = new FrameResetTool(session(driver));

  @Test
  void switchesToDefaultContent() {
    when(driver.switchTo()).thenReturn(targetLocator);

    McpSchema.CallToolResult result = tool.execute(Map.of());

    verify(targetLocator).defaultContent();
    assertThat(text(result)).isEqualTo("Switched to default content");
  }

  static BrowserSession session(SelenideDriver driver) {
    BrowserSession session = mock();
    when(session.getDriver()).thenReturn(driver);
    return session;
  }
}
