package com.github.selenide;

import org.junit.Test;
import org.openqa.selenium.By;

import static com.github.selenide.DOM.getJQuerySelector;
import static org.junit.Assert.assertEquals;

public class DOMTest {
  @Test
  public void canCreateJQuerySelectorForSeleniumSelectors() {
    assertEquals("$(\"*[name='products[0].quantity']\")", getJQuerySelector(By.name("products[0].quantity")));
    assertEquals("$(\"#addProductButton\")", getJQuerySelector(By.id("addProductButton")));
    assertEquals("$(\".product_row\")", getJQuerySelector(By.className("product_row")));

    assertEquals("$(\"label[for='operatingAddressSameAsLegalfalse']\")", getJQuerySelector(By.xpath("//label[@for='operatingAddressSameAsLegalfalse']")));
    assertEquals("$(\"a[class='menu_item'][to='review']\")", getJQuerySelector(By.xpath("//a[@class='menu_item'][@to='review']")));
  }
}
