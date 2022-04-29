package com.codeborne.selenide.conditions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EditableTest {
  @Test
  void toStringShouldBeConcise() {
    assertThat(new Editable()).hasToString("editable");
  }
}
