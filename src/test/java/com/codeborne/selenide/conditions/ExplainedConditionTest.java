package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ExplainedConditionTest {
  private final Condition visible = new Visible().because("I see it");
  private final Condition hidden = new Hidden().because("I don't see it");
  private final Condition text = new Text("blah").because("I typed it");

  @Test
  void negate() {
    assertThat(visible.missingElementSatisfiesCondition()).isFalse();
    assertThat(visible.negate().missingElementSatisfiesCondition()).isTrue();

    assertThat(hidden.missingElementSatisfiesCondition()).isTrue();
    assertThat(hidden.negate().missingElementSatisfiesCondition()).isFalse();

    assertThat(text.missingElementSatisfiesCondition()).isFalse();
    assertThat(text.negate().missingElementSatisfiesCondition()).isFalse();
  }

  @Test
  void name_visible() {
    assertThat(visible.getName()).isEqualTo("visible");
    assertThat(visible.negate().getName()).isEqualTo("not visible");
  }

  @Test
  void name_hidden() {
    assertThat(hidden.getName()).isEqualTo("hidden");
    assertThat(hidden.negate().getName()).isEqualTo("not hidden");
  }

  @Test
  void name_haveText() {
    assertThat(text.getName()).isEqualTo("text");
    assertThat(text.negate().getName()).isEqualTo("not text");
  }

  @Test
  void toString_visible() {
    assertThat(visible.toString()).isEqualTo("visible (because I see it)");
    assertThat(visible.negate().toString()).isEqualTo("not visible (because I see it)");
  }

  @Test
  void toString_hidden() {
    assertThat(hidden.toString()).isEqualTo("hidden (because I don't see it)");
    assertThat(hidden.negate().toString()).isEqualTo("not hidden (because I don't see it)");
  }

  @Test @Disabled
  void toString_haveText() {
    assertThat(text.toString()).isEqualTo("text 'blah' (because I typed it)");
    assertThat(text.negate().toString()).isEqualTo("not text 'blah' (because I typed it)");
  }
}
