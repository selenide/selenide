package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

final class OrTest {

  @Test
  void ctorOfEmptyConditionsListThrowsException() {
    assertThatCode(() -> {
      new Or("", emptyList());
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void ofSingleConditionToString() {
    assertThat(
      new Or("CONDITION_NAME", singletonList(
        new SimpleCondition(true)
      ))
    ).hasToString("CONDITION_NAME: SimpleCondition(true, false)");
  }

  @Test
  void ofConditionsListToString() {
    assertThat(
      new Or("CONDITION_NAME", asList(
        new SimpleCondition(true),
        new SimpleCondition(false)
      ))
    ).hasToString("CONDITION_NAME: SimpleCondition(true, false) or SimpleCondition(false, false)");
  }

  @Test
  void applyMethodReturnsValueFitsTheORLogicOperator() {
    assertThat(
      new Or("", asList(
        new SimpleCondition(true),
        new SimpleCondition(true)
      )).apply(mock(Driver.class), mock(WebElement.class))
    ).isTrue();

    assertThat(
      new Or("", asList(
        new SimpleCondition(true),
        new SimpleCondition(false)
      )).apply(mock(Driver.class), mock(WebElement.class))
    ).isTrue();

    assertThat(
      new Or("", asList(
        new SimpleCondition(false),
        new SimpleCondition(true)
      )).apply(mock(Driver.class), mock(WebElement.class))
    ).isTrue();

    assertThat(
      new Or("", asList(
        new SimpleCondition(false),
        new SimpleCondition(false)
      )).apply(mock(Driver.class), mock(WebElement.class))
    ).isFalse();
  }

  @Test
  void negativeConditionApplyMethodReturnsValueFitsTheNOTLogicOperator() {
    assertThat(
      new Or("", asList(
        new SimpleCondition(true),
        new SimpleCondition(true)
      )).negate().apply(mock(Driver.class), mock(WebElement.class))
    ).isFalse();

    assertThat(
      new Or("", asList(
        new SimpleCondition(true),
        new SimpleCondition(false)
      )).negate().apply(mock(Driver.class), mock(WebElement.class))
    ).isFalse();

    assertThat(
      new Or("", asList(
        new SimpleCondition(false),
        new SimpleCondition(true)
      )).negate().apply(mock(Driver.class), mock(WebElement.class))
    ).isFalse();

    assertThat(
      new Or("", asList(
        new SimpleCondition(false),
        new SimpleCondition(false)
      )).negate().apply(mock(Driver.class), mock(WebElement.class))
    ).isTrue();
  }

  @Test
  void applyNullMethodReturnsFalseOnlyIfAllConditionsReturnFalse() {
    assertThat(
      new Or("", asList(
        new SimpleCondition(true, false),
        new SimpleCondition(true, false),
        new SimpleCondition(true, false)
      )).applyNull()
    ).isFalse();
  }

  @Test
  void applyNullMethodReturnsTrueIfAtLeastOneOfConditionsReturnTrue() {
    assertThat(
      new Or("", asList(
        new SimpleCondition(true, true),
        new SimpleCondition(true, false),
        new SimpleCondition(true, false)
      )).applyNull()
    ).isTrue();

    assertThat(
      new Or("", asList(
        new SimpleCondition(true, false),
        new SimpleCondition(true, false),
        new SimpleCondition(true, true)
      )).applyNull()
    ).isTrue();

    assertThat(
      new Or("", asList(
        new SimpleCondition(false, true),
        new SimpleCondition(false, true),
        new SimpleCondition(false, true)
      )).applyNull()
    ).isTrue();
  }

  @Test
  void negativeConditionApplyNullMethodRecalculatedResultForNegativeInnerConditions() {
    assertThat(
      new Or("", asList(
        new SimpleCondition(false, false),
        new SimpleCondition(false, false)
      )).negate().applyNull()
    ).isTrue();

    assertThat(
      new Or("", asList(
        new SimpleCondition(false, true),
        new SimpleCondition(false, true)
      )).negate().applyNull()
    ).isFalse();

    assertThat(
      new Or("", asList(
        new SimpleCondition(false, true),
        new SimpleCondition(false, false)
      )).negate().applyNull()
    ).isTrue();
  }

  @ParametersAreNonnullByDefault
  private static class SimpleCondition extends Condition {
    private final boolean applyResult;

    SimpleCondition(boolean applyResult) {
      this(applyResult, false);
    }

    SimpleCondition(boolean applyResult, boolean applyNull) {
      super("", applyNull);
      this.applyResult = applyResult;
    }

    @Override
    public boolean apply(Driver driver, WebElement element) {
      return this.applyResult;
    }

    @Nonnull
    @Override
    public Condition negate() {
      return new Not(this, !this.applyNull());
    }

    @Override
    public String toString() {
      return "SimpleCondition(" + this.applyResult + ", " + this.applyNull() + ")";
    }
  }
}
