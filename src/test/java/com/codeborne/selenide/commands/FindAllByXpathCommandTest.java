package com.codeborne.selenide.commands;

import java.util.Collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindAllByXpathCommandTest {
  private SelenideElement parent;
  private WebElementSource locator;
  private SelenideElement element1;
  private FindAllByXpath findAllByXpathCommand;
  private String defaultText = "Default Text";

  @BeforeEach
  void setup() {
    findAllByXpathCommand = new FindAllByXpath();
    parent = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element1 = mock(SelenideElement.class);
    when(element1.getText()).thenReturn(defaultText);
    when(element1.isSelected()).thenReturn(true);
  }

  @Test
  void testExecuteMethodWithNoArgsPassed() {
    Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> findAllByXpathCommand.execute(parent, locator));
  }

  @Test
  void testExecuteMethodWithZeroLengthArgs() {
    when(parent.findElements(By.xpath("."))).thenReturn(Collections.singletonList(element1));
    ElementsCollection findAllCommandCollection = findAllByXpathCommand.execute(parent, locator, ".");
    Assertions.assertEquals(defaultText, findAllCommandCollection.first().getText());
  }

  @Test
  void testExecuteMethodWithMoreThenOneArgsList() {
    when(parent.findElements(By.xpath("."))).thenReturn(Collections.singletonList(element1));
    ElementsCollection findAllCommandCollection = findAllByXpathCommand.execute(parent, locator, ".", "/..");
    Assertions.assertEquals(defaultText, findAllCommandCollection.first().getText());
  }
}
