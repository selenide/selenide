package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.openqa.selenium.By;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetParentCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private Find findMock;
  private GetParent getParentCommand;

  @Before
  public void setup() {
    findMock = mock(Find.class);
    getParentCommand = new GetParent(findMock);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(findMock.execute(proxy, locator, By.xpath(".."), 0)).thenReturn(mockedElement);
  }

  @Test
  public void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    GetParent getParentCommand = new GetParent();
    Field findField = getParentCommand.getClass().getDeclaredField("find");
    findField.setAccessible(true);
    Find find = (Find) findField.get(getParentCommand);
    assertNotNull(find);
  }

  @Test
  public void testExecuteMethod() {
    assertEquals(mockedElement, getParentCommand.execute(proxy, locator, new Object[] {"..", "something more"}));
  }
}
