package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Mocks.mockElement;
import static com.codeborne.selenide.Mocks.mockSelect;
import static com.codeborne.selenide.Mocks.option;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;

final class TextCaseSensitiveTest {

  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(PARTIAL_TEXT));
  private final WebElement elementShort = mockElement("One");
  private final WebElement elementLong = mockElement("ZeroOneTwo");
  private final WebElement singleSelectElement = mockSelect(option("One", true), option("Two"), option("Three"));
  private final WebElement multiSelectElement = mockSelect(option("One", true), option("Two", true), option("Three", true));

  @Test
  void shouldMatchExpectedTextWithSameCase() {
    assertThat(new CaseSensitiveText("One").check(driver, elementShort).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatchExpectedTextWithDifferentCase() {
    assertThat(new CaseSensitiveText("one").check(driver, elementShort).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldNotMatchDifferentExpectedText() {
    assertThat(new CaseSensitiveText("Two").check(driver, elementShort).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldMatchExpectedTextWithinOtherText() {
    assertThat(new CaseSensitiveText("One").check(driver, elementLong).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldMatchExpectedTextInSelectedOptions() {
    assertThat(new CaseSensitiveText("One").check(driver, singleSelectElement).verdict()).isEqualTo(ACCEPT);

    assertThat(new CaseSensitiveText("Two").check(driver, multiSelectElement).verdict()).isEqualTo(ACCEPT);
    assertThat(new CaseSensitiveText("OneTwo").check(driver, multiSelectElement).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatchExpectedTextWithDifferentCaseInSelectedOptions() {
    assertThat(new CaseSensitiveText("one").check(driver, singleSelectElement).verdict()).isEqualTo(REJECT);

    assertThat(new CaseSensitiveText("one").check(driver, multiSelectElement).verdict()).isEqualTo(REJECT);
    assertThat(new CaseSensitiveText("oneTwo").check(driver, multiSelectElement).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldNotMatchExpectedTextInNonSelectedOptions() {
    assertThat(new CaseSensitiveText("Two").check(driver, singleSelectElement).verdict()).isEqualTo(REJECT);
    assertThat(new CaseSensitiveText("Three").check(driver, singleSelectElement).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(new CaseSensitiveText("One")).hasToString("text case sensitive \"One\"");
  }
}
