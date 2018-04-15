package com.codeborne.selenide.commands;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FindAllByXpathCommandTest {
  private SelenideElement parent;
  private WebElementSource locator;
  private SelenideElement element1;
  private FindAllByXpath findAllByXpathCommand;
  private String defaultText = "Default Text";

  @Before
  public void setup() {
    findAllByXpathCommand = new FindAllByXpath();
    parent = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    element1 = mock(SelenideElement.class);
    when(element1.getText()).thenReturn(defaultText);
    when(element1.isSelected()).thenReturn(true);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testExecuteMethodWithNoArgsPassed() {
    findAllByXpathCommand.execute(parent, locator);
  }

  @Test
  public void testExecuteMethodWithZeroLengthArgs() {
    when(parent.findElements(By.xpath("."))).thenReturn(Collections.singletonList(element1));
    ElementsCollection findAllCommandCollection = findAllByXpathCommand.execute(parent, locator, ".");
    assertEquals(defaultText, findAllCommandCollection.first().getText());
  }

  @Test
  public void testExecuteMethodWithMoreThenOneArgsList() {
    when(parent.findElements(By.xpath("."))).thenReturn(Collections.singletonList(element1));
    ElementsCollection findAllCommandCollection = findAllByXpathCommand.execute(parent, locator, ".", "/..");
    assertEquals(defaultText, findAllCommandCollection.first().getText());
  }
}
