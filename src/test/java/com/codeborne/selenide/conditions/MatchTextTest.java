package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class MatchTextTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(PARTIAL_TEXT));

  @Test
  void shouldMatchWholeString() {
    assertThat(new MatchText("Chuck Norris' gmail account is gmail@chuck.norris")
      .check(driver, element("Chuck Norris' gmail account is gmail@chuck.norris")).verdict())
      .isEqualTo(ACCEPT);

    assertThat(new MatchText("Chuck Norris.* gmail\\s+account is gmail@chuck.norris")
      .check(driver, element("Chuck Norris' gmail    account is gmail@chuck.norris")).verdict())
      .isEqualTo(ACCEPT);
  }

  @Test
  void shouldMatchSubstring() {
    assertThat(new MatchText("Chuck")
      .check(driver, element("Chuck Norris' gmail account is gmail@chuck.norris")).verdict())
      .isEqualTo(ACCEPT);
    assertThat(new MatchText("Chuck\\s*Norris")
      .check(driver, element("Chuck Norris' gmail account is gmail@chuck.norris")).verdict())
      .isEqualTo(ACCEPT);
    assertThat(new MatchText("gmail account")
      .check(driver, element("Chuck Norris' gmail account is gmail@chuck.norris")).verdict())
      .isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatch() {
    MatchText condition = new MatchText("selenide");
    assertThat(condition.check(driver, element("selenite")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(new MatchText("Chuck Norris")).hasToString("match text \"Chuck Norris\"");
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    MatchText condition = new MatchText("Chuck");
    WebElement element = element("Chuck Norris' gmail account");

    CheckResult checkResult = condition.check(driver, element);

    assertThat(checkResult.actualValue()).isEqualTo("text=\"Chuck Norris' gmail account\"");
    verify(element).getText();
    verifyNoMoreInteractions(element);
  }

  private WebElement element(String text) {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn(text);
    return element;
  }
}
