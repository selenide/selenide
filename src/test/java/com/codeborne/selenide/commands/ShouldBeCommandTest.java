package com.codeborne.selenide.commands;

import java.lang.reflect.Field;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShouldBeCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private ShouldBe shouldBeCommand;
  private WebElement mockedFoundElement;

  @BeforeEach
  void setup() {
    shouldBeCommand = new ShouldBe();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    ShouldBe shouldBe = new ShouldBe();
    Field prefixField = shouldBe.getClass().getSuperclass().getDeclaredField("prefix");
    prefixField.setAccessible(true);
    String prefix = (String) prefixField.get(shouldBe);
    Assertions.assertEquals("be ", prefix);
  }

  @Test
  void testExecuteMethodWithNonStringArgs() {
    SelenideElement returnedElement = shouldBeCommand.execute(proxy, locator, new Object[]{Condition.disabled});
    Assertions.assertEquals(proxy, returnedElement);
  }

  @Test
  void testExecuteMethodWithStringArgs() {
    SelenideElement returnedElement = shouldBeCommand.execute(proxy, locator, new Object[]{"hello"});
    Assertions.assertEquals(proxy, returnedElement);
  }
}
