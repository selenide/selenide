package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Cok on 03.06.2017.
 */
public class ElementSupplierTest {

  @Test
  public void getSearchCriteriaShouldCallDescribeMethod() throws Exception {
    WebElement mock = mock(WebElement.class);
    when(Describe.describe(mock)).thenReturn("was triggered");
    String actualValue = new ElementSupplier(() -> mock).getSearchCriteria();
    assertThat(actualValue, containsString("was triggered"));
  }

  @Test
  public void whenExceptionIsComingFromDescribeSearchCriteriaShouldReturnMessageOfException() throws Exception {
    WebElement mock = mock(WebElement.class);
    when(Describe.describe(mock)).thenThrow(new StaleElementReferenceException("element changed"));
    String actualValue = new ElementSupplier(() -> mock).getSearchCriteria();
    assertThat(actualValue, equalTo("StaleElementReferenceException: element changed"));
  }
}
