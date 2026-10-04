package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.SelenideTargetLocator;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.Map;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FrameSelectToolTest {
  private final SelenideDriver driver = mock();
  private final SelenideElement element = mock();
  private final SelenideTargetLocator targetLocator = mock();
  private final FrameSelectTool tool = new FrameSelectTool(session(driver));

  @Test
  void switchesIntoFrameBySelector() {
    when(driver.$(any(By.class))).thenReturn(element);
    when(driver.switchTo()).thenReturn(targetLocator);

    McpSchema.CallToolResult result = tool.execute(Map.of("selector", "#iframe"));

    verify(targetLocator).frame(element);
    assertThat(text(result)).isEqualTo("Switched into frame: #iframe");
  }

  static BrowserSession session(SelenideDriver driver) {
    BrowserSession session = mock();
    when(session.getDriver()).thenReturn(driver);
    return session;
  }
}
