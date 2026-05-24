package com.codeborne.selenide.mcp.tools;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WaitForToolTest {

  @Test
  void rejectsZeroConditions() {
    assertThatThrownBy(() -> WaitForTool.parseCondition(Map.of()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("exactly one of");
  }

  @Test
  void rejectsMultipleConditions() {
    Map<String, Object> args = new HashMap<>();
    args.put("text", "hello");
    args.put("time", 1);
    assertThatThrownBy(() -> WaitForTool.parseCondition(args))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("exactly one of");
  }

  @Test
  void parsesTextCondition() {
    WaitForTool.Condition c = WaitForTool.parseCondition(Map.of("text", "hello"));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.TEXT);
    assertThat(c.value()).isEqualTo("hello");
  }

  @Test
  void parsesTextGoneCondition() {
    WaitForTool.Condition c = WaitForTool.parseCondition(Map.of("textGone", "bye"));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.TEXT_GONE);
    assertThat(c.value()).isEqualTo("bye");
  }

  @Test
  void parsesSelectorConditionDefaultsToVisible() {
    WaitForTool.Condition c = WaitForTool.parseCondition(Map.of("selector", "#foo"));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.SELECTOR_VISIBLE);
    assertThat(c.value()).isEqualTo("#foo");
  }

  @Test
  void parsesSelectorConditionHidden() {
    WaitForTool.Condition c = WaitForTool.parseCondition(
      Map.of("selector", "#foo", "state", "hidden"));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.SELECTOR_HIDDEN);
    assertThat(c.value()).isEqualTo("#foo");
  }

  @Test
  void parsesTimeCondition() {
    WaitForTool.Condition c = WaitForTool.parseCondition(Map.of("time", 2.5));
    assertThat(c.kind()).isEqualTo(WaitForTool.Kind.TIME);
    assertThat(c.numericValue()).isEqualTo(2.5);
  }

  @Test
  void rejectsUnknownSelectorState() {
    assertThatThrownBy(() -> WaitForTool.parseCondition(
      Map.of("selector", "#foo", "state", "weird")))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("state");
  }
}
