package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.conditions.PseudoElementPropertyWithValue.JS_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class PseudoElementPropertyWithValueTest {

  private final Driver driver = mock();
  private final WebElement element = mock();

  @BeforeEach
  void setUp() {
    when(driver.executeJavaScript(JS_CODE, element, ":before", "content")).thenReturn("hello");
  }

  @Test
  void apply() {
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "hello")
      .check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "Hello")
      .check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "dummy")
      .check(driver, element).verdict()).isEqualTo(REJECT);
  }

  @Test
  void actualValue() {
    PseudoElementPropertyWithValue condition = new PseudoElementPropertyWithValue(":before", "content", "hello");
    CheckResult checkResult = condition.check(driver, element);
    assertThat(checkResult.actualValue()).isEqualTo(":before {content: hello;}");
  }

  @Test
  void tostring() {
    assertThat(new PseudoElementPropertyWithValue(":before", "content", "hello"))
      .hasToString("pseudo-element :before {content: hello;}");
  }
}
