package com.codeborne.selenide.conditions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class NamedConditionTest {
  private final Visible visible = new Visible();
  private final Hidden hidden = new Hidden();
  private final Text text = new Text("blah");

  @Test
  void negate() {
    assertThat(new NamedCondition("be", visible).missingElementSatisfiesCondition()).isFalse();
    assertThat(new NamedCondition("be", visible).negate().missingElementSatisfiesCondition()).isTrue();

    assertThat(new NamedCondition("be", hidden).missingElementSatisfiesCondition()).isTrue();
    assertThat(new NamedCondition("be", hidden).negate().missingElementSatisfiesCondition()).isFalse();

    assertThat(new NamedCondition("have", text).missingElementSatisfiesCondition()).isFalse();
    assertThat(new NamedCondition("have", text).negate().missingElementSatisfiesCondition()).isFalse();
  }

  @Test
  void name() {
    assertThat(new NamedCondition("be", visible).getName()).isEqualTo("visible");
    assertThat(new NamedCondition("be", visible).negate().getName()).isEqualTo("not visible");
  }
}
