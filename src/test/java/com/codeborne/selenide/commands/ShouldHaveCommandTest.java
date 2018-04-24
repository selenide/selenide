package com.codeborne.selenide.commands;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShouldHaveCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private ShouldHave shouldHaveCommand;
  private WebElement mockedFoundElement;

  @Before
  public void setup() {
    shouldHaveCommand = new ShouldHave();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedFoundElement = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedFoundElement);
  }

  @Test
  public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    ShouldHave shouldHave = new ShouldHave();
    Field prefixField = shouldHave.getClass().getSuperclass().getDeclaredField("prefix");
    prefixField.setAccessible(true);
    String prefix = (String) prefixField.get(shouldHave);
    assertEquals("have ", prefix);
  }

  @Test
  public void testExecuteMethodWithNonStringArgs() {
    SelenideElement returnedElement = shouldHaveCommand.execute(proxy, locator, new Object[]{Condition.disabled});
    assertEquals(proxy, returnedElement);
  }

  @Test
  public void testExecuteMethodWithStringArgs() {
    SelenideElement returnedElement = shouldHaveCommand.execute(proxy, locator, new Object[]{"hello"});
    assertEquals(proxy, returnedElement);
  }
}
