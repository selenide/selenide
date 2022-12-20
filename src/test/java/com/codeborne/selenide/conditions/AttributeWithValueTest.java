package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class AttributeWithValueTest {
  private final Driver driver = mock();
  private final WebElement element = mock();

  @BeforeEach
  void setUp() {
    when(element.getAttribute("data-id")).thenReturn("actual");
  }

  @Test
  void check() {
    assertThat(new AttributeWithValue("data-id", "actual").check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(new AttributeWithValue("data-id", "expected").check(driver, element).verdict()).isEqualTo(REJECT);
  }

  @Test
  void actualValue() {
    assertThat(new AttributeWithValue("data-id", "expected").check(driver, element).actualValue()).isEqualTo("data-id=\"actual\"");
  }

  @Test
  void tostring() {
    assertThat(new AttributeWithValue("data-id", "expected")).hasToString("attribute data-id=\"expected\"");
  }
}
