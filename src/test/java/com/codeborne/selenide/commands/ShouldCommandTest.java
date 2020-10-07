package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ShouldCommandTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final Should shouldCommand = new Should();
  private final WebElement mockedFoundElement = mock(WebElement.class);

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    Should should = new Should();
    Field prefixField = should.getClass().getDeclaredField("prefix");
    prefixField.setAccessible(true);
    String prefix = (String) prefixField.get(should);
    assertThat(prefix.isEmpty())
      .isTrue();
  }

  @Test
  void testExecuteMethodWithNonStringArgs() {
    SelenideElement returnedElement = shouldCommand.execute(proxy, locator, new Object[]{Condition.disabled});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }

  @Test
  void testExecuteMethodWithStringArgs() {
    SelenideElement returnedElement = shouldCommand.execute(proxy, locator, new Object[]{"hello"});
    assertThat(returnedElement)
      .isEqualTo(proxy);
  }
}
