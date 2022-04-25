package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExactTextCaseSensitiveTest {

  private final Driver driver = new DriverStub();
  private final WebElement element = mock(WebElement.class);

  private final ExactTextCaseSensitive condition = new ExactTextCaseSensitive("John Malkovich");

  @BeforeEach
  void setUp() {
    when(element.getText()).thenReturn("John Malkovich");
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(element);
  }

  @Test
  void shouldMatchExpectedTextWithSameCase() {
    assertThat(condition.check(driver, element).verdict()).isEqualTo(ACCEPT);
    verify(element).getText();
  }

  @Test
  void shouldNotMatchExpectedTextWithDifferentCase() {
    ExactTextCaseSensitive condition = new ExactTextCaseSensitive("john Malkovich");
    assertThat(condition.check(driver, element).verdict()).isEqualTo(REJECT);
    verify(element).getText();
  }

  @Test
  void shouldNotMatchDifferentExpectedText() {
    ExactTextCaseSensitive condition = new ExactTextCaseSensitive("John");
    assertThat(condition.check(driver, element).verdict()).isEqualTo(REJECT);
    verify(element).getText();
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(condition).hasToString("exact text case sensitive \"John Malkovich\"");
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    ExactTextCaseSensitive condition = new ExactTextCaseSensitive("Two");
    CheckResult checkResult = condition.check(driver, element);

    assertThat(checkResult.verdict()).isEqualTo(REJECT);
    assertThat(checkResult.actualValue()).isEqualTo("text=\"John Malkovich\"");
    verify(element).getText();
  }
}
