package com.codeborne.selenide;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;

public class JQueryTest {
  JQuery jQuery = new JQuery();

  @Test @Ignore
  public void canCreateJQuerySelectorForSeleniumSelectors_old() {
    assertEquals("$(\"*[name='products[0].quantity']\")", jQuery.getJQuerySelector(By.name("products[0].quantity")));
    assertEquals("$(\"#addProductButton\")", jQuery.getJQuerySelector(By.id("addProductButton")));
    assertEquals("$(\".product_row\")", jQuery.getJQuerySelector(By.className("product_row")));

    assertEquals("$(\"label[for='operatingAddressSameAsLegalfalse']\")", jQuery.getJQuerySelector(By.xpath("//label[@for='operatingAddressSameAsLegalfalse']")));
    assertEquals("$(\"a[class='menu_item'][to='review']\")", jQuery.getJQuerySelector(By.xpath("//a[@class='menu_item'][@to='review']")));
  }

  @Test
  public void canCreateJQuerySelectorForSeleniumSelectors() {
    assertEquals("[name='products[0].quantity']", jQuery.getJQuerySelector(By.name("products[0].quantity")));
    assertEquals("#addProductButton", jQuery.getJQuerySelector(By.id("addProductButton")));
    assertEquals(".product_row", jQuery.getJQuerySelector(By.className("product_row")));
    assertEquals("form input.hello", jQuery.getJQuerySelector(By.cssSelector("form input.hello")));

    assertEquals("label[for='operatingAddressSameAsLegalfalse']", jQuery.getJQuerySelector(By.xpath("//label[@for='operatingAddressSameAsLegalfalse']")));
    assertEquals("a[class='menu_item'][to='review']", jQuery.getJQuerySelector(By.xpath("//a[@class='menu_item'][@to='review']")));
  }
}
