package com.codeborne.selenide;

import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.DOM.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DOMTest {
  @Test
  public void canCreateJQuerySelectorForSeleniumSelectors() {
    assertEquals("$(\"*[name='products[0].quantity']\")", getJQuerySelector(By.name("products[0].quantity")));
    assertEquals("$(\"#addProductButton\")", getJQuerySelector(By.id("addProductButton")));
    assertEquals("$(\".product_row\")", getJQuerySelector(By.className("product_row")));

    assertEquals("$(\"label[for='operatingAddressSameAsLegalfalse']\")", getJQuerySelector(By.xpath("//label[@for='operatingAddressSameAsLegalfalse']")));
    assertEquals("$(\"a[class='menu_item'][to='review']\")", getJQuerySelector(By.xpath("//a[@class='menu_item'][@to='review']")));
  }
  
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

  @Test
  public void worksForPagesWithoJQuery() {
    Navigation.navigateToAbsoluteUrl("http://jquery.com/"); // TODO Use something like localhost:8080/with-jquery
    assertTrue(DOM.isJQueryAvailable());
    // TODO Test all methods on this page
  }
}
