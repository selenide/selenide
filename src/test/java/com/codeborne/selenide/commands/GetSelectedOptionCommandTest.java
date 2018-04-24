package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetSelectedOptionCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private String mockedElement1Text = "Element text2";
  private GetSelectedOption getSelectedOptionCommand;

  @Before
  public void setup() {
    getSelectedOptionCommand = new GetSelectedOption();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    SelenideElement mockedElement = mock(SelenideElement.class);
    WebElement mockedElement1 = mock(WebElement.class);
    WebElement mockedElement2 = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isSelected()).thenReturn(true);
    when(mockedElement.getTagName()).thenReturn("select");
    when(mockedElement.findElements(By.tagName("option"))).thenReturn(asList(mockedElement1, mockedElement2));
    when(mockedElement1.isSelected()).thenReturn(true);
    when(mockedElement1.getText()).thenReturn(mockedElement1Text);
    when(mockedElement2.isSelected()).thenReturn(false);
  }

  @Test
  public void testExecuteMethod() {
    SelenideElement selectedElement = getSelectedOptionCommand.execute(proxy, locator, new Object[]{"something more"});
    assertEquals(mockedElement1Text, selectedElement.getText());
  }
}
