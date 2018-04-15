package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetSearchCriteriaCommandTest {
  private SelenideElement proxy;
  private WebElementSource locator;
  private GetSearchCriteria getSearchCriteriaCommand;
  private String defaultSearchCriteria = "by.xpath";

  @Before
  public void setup() {
    getSearchCriteriaCommand = new GetSearchCriteria();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    when(locator.getSearchCriteria()).thenReturn(defaultSearchCriteria);
  }

  @Test
  public void testExecuteMethod() {
    assertEquals(defaultSearchCriteria, getSearchCriteriaCommand.execute(proxy, locator, new Object[]{"something more"}));
  }
}
