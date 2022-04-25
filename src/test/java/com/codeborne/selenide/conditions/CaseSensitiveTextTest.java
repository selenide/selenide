package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CaseSensitiveTextTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(PARTIAL_TEXT));
  private final WebElement element = mockWebElement("div", "");

  private final CaseSensitiveText condition = new CaseSensitiveText("John Malkovich");

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(element);
  }

  @Test
  void shouldMatchExpectedPartOfActualTextWithSameCase() {
    when(element.getText()).thenReturn("John Malkovich The First");

    assertThat(condition.check(driver, element).verdict()).isEqualTo(ACCEPT);
    verify(element).getTagName();
    verify(element).getText();
  }

  @Test
  void shouldMatchExpectedPartOfActualTextWithSameCaseIgnoresWhitespaces() {
    when(element.getText()).thenReturn("John Malkovich\t The   \n First");

    assertThat(new CaseSensitiveText("John        Malkovich The   ").check(driver, element).verdict()).isEqualTo(ACCEPT);
    verify(element).getTagName();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchExpectedPartOfActualTextWithDifferentCase() {
    when(element.getText()).thenReturn("john Malkovich the first");

    assertThat(condition.check(driver, element).verdict()).isEqualTo(REJECT);
    verify(element).getTagName();
    verify(element).getText();
  }

  @Test
  void shouldNotMatchActualPartOfExpectedText() {
    when(element.getText()).thenReturn("John");

    assertThat(condition.check(driver, element).verdict()).isEqualTo(REJECT);
    verify(element).getTagName();
    verify(element).getText();
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(condition).hasToString("text case sensitive \"John Malkovich\"");
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    when(element.getText()).thenReturn("John");
    CheckResult checkResult = condition.check(driver, element);

    assertThat(checkResult.actualValue()).isEqualTo("text=\"John\"");
    verify(element).getTagName();
    verify(element).getText();
  }
}
