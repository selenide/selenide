package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class MatchTextTest implements WithAssertions {
  private final Driver driver = mock(Driver.class);

  @Test
  void shouldMatchWholeString() {
    assertThat(new MatchText("Chuck Norris' gmail account is gmail@chuck.norris")
      .apply(driver, element("Chuck Norris' gmail account is gmail@chuck.norris")))
      .isTrue();

    assertThat(new MatchText("Chuck Norris.* gmail\\s+account is gmail@chuck.norris")
      .apply(driver, element("Chuck Norris' gmail    account is gmail@chuck.norris")))
      .isTrue();
  }

  @Test
  void shouldMatchSubstring() {
    assertThat(new MatchText("Chuck")
      .apply(driver, element("Chuck Norris' gmail account is gmail@chuck.norris")))
      .isTrue();
    assertThat(new MatchText("Chuck\\s*Norris")
      .apply(driver, element("Chuck Norris' gmail account is gmail@chuck.norris")))
      .isTrue();
    assertThat(new MatchText("gmail account")
      .apply(driver, element("Chuck Norris' gmail account is gmail@chuck.norris")))
      .isTrue();
  }

  @Test
  void shouldNotMatch() {
    assertThat(new MatchText("selenide").apply(driver, element("selenite"))).isFalse();
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(new MatchText("Chuck Norris")).hasToString("match text 'Chuck Norris'");
  }

  @Test
  void shouldNotHaveActualValueBeforeAnyMatching() {
    WebElement element = element("Norris' gmail account");

    assertThat(new MatchText("Chuck").actualValue(driver, element)).isNull();
    verifyNoMoreInteractions(driver, element);
  }

  @Test
  void shouldHaveCorrectActualValueAfterMatching() {
    MatchText condition = new MatchText("Chuck");
    WebElement element = element("Chuck Norris' gmail account");
    condition.apply(driver, element);

    assertThat(condition.actualValue(driver, element)).isEqualTo("Chuck Norris' gmail account");
    verify(element).getText();
    verifyNoMoreInteractions(driver, element);
  }

  private WebElement element(String text) {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn(text);
    return element;
  }
}
