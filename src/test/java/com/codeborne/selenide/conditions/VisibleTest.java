package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class VisibleTest {
  private final Driver driver = mock();
  private final Visible condition = new Visible();
  private final WebElement element = mock();

  @Test
  void negate() {
    assertThat(condition.missingElementSatisfiesCondition()).isFalse();
    assertThat(condition.negate().missingElementSatisfiesCondition()).isTrue();
  }

  @Test
  void name() {
    assertThat(condition.getName()).isEqualTo("visible");
    assertThat(condition.negate().getName()).isEqualTo("not visible");
  }

  @Test
  void satisfied_if_element_is_visible() {
    when(element.isDisplayed()).thenReturn(true);
    assertThat(condition.check(driver, element).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void not_satisfied_if_element_is_invisible() {
    when(element.isDisplayed()).thenReturn(false);
    assertThat(condition.check(driver, element).verdict()).isEqualTo(REJECT);
  }

  @Test
  void actualValue_invisible() {
    assertThat(condition.check(driver, element).actualValue()).isEqualTo("hidden");
  }

  @Test
  void actualValue_visible() {
    when(element.isDisplayed()).thenReturn(true);
    assertThat(condition.check(driver, element).actualValue()).isEqualTo("visible");
  }
}
