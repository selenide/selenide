package com.codeborne.selenide.commands;


import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class GetLastChildTest implements WithAssertions {
  private final SelenideElement proxy = mock(SelenideElement.class);
  private final WebElementSource locator = mock(WebElementSource.class);
  private final SelenideElement mockedElement = mock(SelenideElement.class);
  private final Find findMock = mock(Find.class);
  private final GetLastChild getLastChildCommand = new GetLastChild(findMock);

  @BeforeEach
  void setup() {
    when(locator.getWebElement()).thenReturn(mockedElement);
    when(findMock.execute(proxy, locator, By.xpath("*[last()]"), 0)).thenReturn(mockedElement);
  }

  @Test
  void testDefaultConstructor() throws NoSuchFieldException, IllegalAccessException {
    GetLastChild getLastChild = new GetLastChild();
    Field findField = getLastChild.getClass().getDeclaredField("find");
    findField.setAccessible(true);
    Find find = (Find) findField.get(getLastChild);
    assertThat(find)
      .isNotNull();
  }

  @Test
  void testExecuteMethod() {
    assertThat(getLastChildCommand.execute(proxy, locator, new Object[]{"*[last()]", "something more"}))
      .isEqualTo(mockedElement);
  }
}
