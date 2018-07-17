package com.codeborne.selenide.commands;

import java.lang.reflect.Field;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetParentCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private SelenideElement mockedElement;
  private Find findMock;
  private GetParent getParentCommand;

  @BeforeEach
  void setup() {
    findMock = mock(Find.class);
    getParentCommand = new GetParent(findMock);
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    mockedElement = mock(SelenideElement.class);
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(findMock.execute(proxy, locator, By.xpath(".."), 0)).thenReturn(mockedElement);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    GetParent getParentCommand = new GetParent();
    Field findField = getParentCommand.getClass().getDeclaredField("find");
    findField.setAccessible(true);
    Find find = (Find) findField.get(getParentCommand);
    assertThat(find)
      .isNotNull();
  }

  @Test
  void testExecuteMethod() {
    assertThat(getParentCommand.execute(proxy, locator, new Object[]{"..", "something more"}))
      .isEqualTo(mockedElement);
  }
}
