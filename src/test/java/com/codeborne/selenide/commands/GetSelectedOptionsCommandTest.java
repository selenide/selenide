package com.codeborne.selenide.commands;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetSelectedOptionsCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private GetSelectedOptions getSelectedOptionsCommand;
  private List<WebElement> mMockedElementsList;

  @Before
  public void setup() {
    getSelectedOptionsCommand = new GetSelectedOptions();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    SelenideElement mockedElement = mock(SelenideElement.class);
    WebElement mockedElement1 = mock(WebElement.class);
    WebElement mockedElement2 = mock(WebElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(mockedElement.isSelected()).thenReturn(true);
    when(mockedElement.getTagName()).thenReturn("select");
    mMockedElementsList = asList(mockedElement1, mockedElement2);
    when(mockedElement.findElements(By.tagName("option"))).thenReturn(mMockedElementsList);
    when(mockedElement1.isSelected()).thenReturn(true);
    when(mockedElement1.getText()).thenReturn("Element text1");
    when(mockedElement2.isSelected()).thenReturn(true);
    when(mockedElement2.getText()).thenReturn("Element text2");
  }

  @Test
  public void testExecuteMethod() {
    ElementsCollection elementsCollection = getSelectedOptionsCommand.execute(proxy, locator, new Object[] {"something more"});
    for (int index = 0; index < elementsCollection.size(); index++) {
      String mockedElementText = mMockedElementsList.get(index).getText();
      String foundElementText = elementsCollection.get(index).getText();
      assertEquals(mockedElementText, foundElementText);
    }
  }
}
