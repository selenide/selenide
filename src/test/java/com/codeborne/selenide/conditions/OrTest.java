package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.attribute;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

final class OrTest {

  @Test
  void verifyToString() {
    Condition or = new Or("checked", asList(
      attribute("checked", "true"),
      attribute("checked", "on")
    ));
    assertThat(or).hasToString("checked: attribute checked=\"true\" or attribute checked=\"on\"");
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

    public SimpleCondition(boolean applyResult) {
      this(applyResult, false);
    }

    public SimpleCondition(boolean applyResult, boolean applyNull) {
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
  }
}
