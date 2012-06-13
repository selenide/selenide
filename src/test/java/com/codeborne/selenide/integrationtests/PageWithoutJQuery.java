package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.DOM;
import com.codeborne.selenide.Navigation;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.DOM.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PageWithoutJQuery {
  @Test
  public void worksForPagesWithoutJQuery() {
    Navigation.navigateToAbsoluteUrl("http://www.rambler.ru"); // TODO Use something like localhost:8080/nojquery
    assertFalse(DOM.isJQueryAvailable());
    selectOption(By.xpath("//select[@name='domain']"), "myrambler.ru");
    assertEquals("myrambler.ru", getSelectedValue(By.xpath("//select[@name='domain']")));
    assertEquals("@myrambler.ru", getSelectedText(By.xpath("//select[@name='domain']")));

    selectOptionByText(By.xpath("//select[@name='domain']"), "@autorambler.ru");
    assertEquals("autorambler.ru", getSelectedValue(By.xpath("//select[@name='domain']")));
    assertEquals("@autorambler.ru", getSelectedText(By.xpath("//select[@name='domain']")));

    // TODO Test all methods on this page
  }
}
