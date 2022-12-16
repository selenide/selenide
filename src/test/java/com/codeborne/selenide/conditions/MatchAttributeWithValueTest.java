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

final class MatchAttributeWithValueTest {
  private final Driver driver = mock();
  private final WebElement element = mock();

  @BeforeEach
  void setUp() {
    when(element.getAttribute("data-id")).thenReturn("actual");
  }

  @Test
  void check() {
    assertThat(new MatchAttributeWithValue("data-id", "act.*").check(driver, element).verdict()).isEqualTo(ACCEPT);
    assertThat(new MatchAttributeWithValue("data-id", "exp.*").check(driver, element).verdict()).isEqualTo(REJECT);
  }

  @Test
  void actualValue() {
    assertThat(new MatchAttributeWithValue("data-id", "expected").check(driver, element).actualValue()).isEqualTo("data-id~/actual/");
  }

  @Test
  void tostring() {
    assertThat(new MatchAttributeWithValue("data-id", "exp.*")).hasToString("match attribute data-id~/exp.*/");
  }
}
