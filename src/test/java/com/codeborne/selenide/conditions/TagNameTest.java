package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

final class TagNameTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(PARTIAL_TEXT));

  @Test
  void apply_for_textInput() {
    assertThat(new TagName("div").check(driver, mockWebElement("div", "")).verdict()).isEqualTo(ACCEPT);
    assertThat(new TagName("div").check(driver, mockWebElement("button", "")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void to_string() {
    assertThat(new TagName("button")).hasToString("tag \"button\"");
  }

  @Test
  void negate_to_string() {
    assertThat(new TagName("table").negate()).hasToString("not tag \"table\"");
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    TagName condition = new TagName("blockquote");
    WebElement element = mockWebElement("cite", "To be or not to be");
    CheckResult checkResult = condition.check(driver, element);

    assertThat(checkResult.actualValue()).isEqualTo("tag \"cite\"");
    verify(element).getTagName();
    verifyNoMoreInteractions(element);
  }
}
