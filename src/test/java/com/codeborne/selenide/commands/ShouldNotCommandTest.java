package com.codeborne.selenide.commands;

import java.lang.reflect.Field;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShouldNotCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private ShouldNot shouldNotCommand;
  private WebElement mockedFoundElement;

  @BeforeEach
  void setup() {
    shouldNotCommand = new ShouldNot();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    ShouldNot shouldNot = new ShouldNot();
    Field prefixField = shouldNot.getClass().getDeclaredField("prefix");
    prefixField.setAccessible(true);
    String prefix = (String) prefixField.get(shouldNot);
    assertThat(prefix)
      .isNullOrEmpty();
  }

  @Test
  void testExecuteMethodWithNonStringArgs() {
    SelenideElement returnedElement = shouldNotCommand.execute(proxy, locator, new Object[]{Condition.disabled});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @Test
  void testExecuteMethodWithStringArgs() {
    SelenideElement returnedElement = shouldNotCommand.execute(proxy, locator, new Object[]{"hello"});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }
}
