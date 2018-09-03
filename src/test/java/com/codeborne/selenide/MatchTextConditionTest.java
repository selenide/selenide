package com.codeborne.selenide;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MatchTextConditionTest implements WithAssertions {
  private Context context = mock(Context.class);

  @Test
  void displaysHumanReadableName() {
    assertThat(Condition.matchText("abc"))
      .hasToString("match text 'abc'");
  }

  @Test
  void matchesWholeString() {
    assertThat(Condition.matchText("Chuck Norris' gmail account is gmail@chuck.norris")
      .apply(context, element("Chuck Norris' gmail account is gmail@chuck.norris")))
      .isTrue();

    assertThat(Condition.matchText("Chuck Norris.* gmail\\s+account is gmail@chuck.norris")
      .apply(context, element("Chuck Norris' gmail    account is gmail@chuck.norris")))
      .isTrue();
  }

  private WebElement element(String text) {
    WebElement element = mock(WebElement.class);
    when(element.getText()).thenReturn(text);
    return element;
  }

  @Test
  void matchesSubstring() {
    assertThat(Condition.matchText("Chuck")
      .apply(context, element("Chuck Norris' gmail account is gmail@chuck.norris")))
      .isTrue();
    assertThat(Condition.matchText("Chuck\\s*Norris")
      .apply(context, element("Chuck Norris' gmail account is gmail@chuck.norris")))
      .isTrue();
    assertThat(Condition.matchText("gmail account")
      .apply(context, element("Chuck Norris' gmail account is gmail@chuck.norris")))
      .isTrue();
  }
}
