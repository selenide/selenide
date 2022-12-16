package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ShouldHaveCommandTest {
  private final SelenideElement proxy = mock();
  private final WebElementSource locator = mock();
  private final ShouldHave shouldHaveCommand = new ShouldHave();
  private final WebElement mockedFoundElement = mock();

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void checksEveryConditionFromGivenParameters() {
    Condition condition1 = text("aaa");
    Condition condition2 = attribute("readonly");
    SelenideElement returnedElement = shouldHaveCommand.execute(proxy, locator, new Object[]{condition1, condition2});
    assertThat(returnedElement).isEqualTo(proxy);
    verify(locator).checkCondition("have ", condition1, false);
    verify(locator).checkCondition("have ", condition2, false);
  }
}
