package com.codeborne.selenide.mcp.tools;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.mcp.BrowserSession;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.mcp.tools.McpToolTestSupport.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FillFormToolTest {
  private final SelenideDriver driver = mock();
  private final SelenideElement element = mock();
  private final FillFormTool tool = new FillFormTool(session(driver));

  @Test
  void fillsEveryFieldInOrder() {
    when(driver.$(any(By.class))).thenReturn(element);

    McpSchema.CallToolResult result = tool.execute(Map.of(
      "fields", List.of(
        Map.of("selector", "#a", "value", "1"),
        Map.of("selector", "#b", "value", "2")
      )
    ));

    verify(element).setValue("1");
    verify(element).setValue("2");
    assertThat(text(result)).isEqualTo("Filled 2 fields");
  }

  @Test
  void rejectsMissingFields() {
    assertThatThrownBy(() -> tool.execute(Map.of()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("non-empty array");
  }

  @Test
  void rejectsEmptyFields() {
    assertThatThrownBy(() -> tool.execute(Map.of("fields", List.of())))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("non-empty array");
  }

  @Test
  void rejectsNullElementInFieldsArray() {
    List<Map<String, Object>> fieldsWithNull = new ArrayList<>();
    fieldsWithNull.add(null);
    fieldsWithNull.add(Map.of("selector", "#a", "value", "1"));

    assertThatThrownBy(() -> tool.execute(Map.of("fields", fieldsWithNull)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("requires both 'selector' and 'value'");
  }

  @Test
  void rejectsFieldMissingSelectorOrValue() {
    assertThatThrownBy(() -> tool.execute(Map.of(
      "fields", List.of(Map.of("value", "1"))
    ))).isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("requires both 'selector' and 'value'");
  }

  static BrowserSession session(SelenideDriver driver) {
    BrowserSession session = mock();
    when(session.getDriver()).thenReturn(driver);
    return session;
  }
}
