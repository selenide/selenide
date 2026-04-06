package com.codeborne.selenide.mcp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ToolErrorHandlerTest {
  private final ToolErrorHandler handler = new ToolErrorHandler();

  @Test
  void formatsGenericError() {
    String result = handler.formatError(
      new RuntimeException("Something went wrong"), "#btn"
    );
    assertThat(result).contains("Something went wrong");
    assertThat(result).contains("#btn");
  }

  @Test
  void includesSelectorInError() {
    String result = handler.formatError(
      new RuntimeException("timeout"), "text=Submit"
    );
    assertThat(result).contains("text=Submit");
  }
}
