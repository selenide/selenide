package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindByXpathCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement element1;
  private FindByXpath findByXpathCommand;

  @BeforeEach
  void setup() {
    findByXpathCommand = new FindByXpath();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element1 = mock(SelenideElement.class);
  }

  @Test
  void testExecuteMethodWithNoArgsPassed() {
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> findByXpathCommand.execute(proxy, locator));
  }

  @Test
  void testExecuteMethodWithZeroLengthArgs() {
    when(locator.find(proxy, By.xpath("."), 0)).thenReturn(element1);
    Assertions.assertEquals(element1, findByXpathCommand.execute(proxy, locator, "."));
  }

  @Test
  void testExecuteMethodWithMoreThenOneArgsList() {
    when(locator.find(proxy, By.xpath("."), 1)).thenReturn(element1);
    Assertions.assertEquals(element1, findByXpathCommand.execute(proxy, locator, ".", 1));
  }
}
