package com.github.selenide;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.github.selenide.UITest.getJQuerySelector;
import static org.junit.Assert.assertEquals;

public class UITestTest {
    @Test
  public void canCreateJQuerySelectorForSeleniumSelectors() throws Exception {
    assertEquals("$(\"*[name='products[0].quantity']\")", getJQuerySelector(By.name("products[0].quantity")));
    assertEquals("$(\"#addProductButton\")", getJQuerySelector(By.id("addProductButton")));
    assertEquals("$(\".product_row\")", getJQuerySelector(By.className("product_row")));

    assertEquals("$(\"label[for='operatingAddressSameAsLegalfalse']\")", getJQuerySelector(By.xpath("//label[@for='operatingAddressSameAsLegalfalse']")));
    assertEquals("$(\"a[class='menu_item'][to='review']\")", getJQuerySelector(By.xpath("//a[@class='menu_item'][@to='review']")));
  }

  @Test
  public void addsRandomNumbersToEveryUrlToAvoidIECaching() {
    UITest uitest = new UITest() {
      @Override
      String generateUID() {
        return "xxx";
      }
    };
    assertEquals("http://chuck-norris.com?timestamp=xxx", uitest.makeUniqueUrl("http://chuck-norris.com"));
    assertEquals("http://chuck-norris.com?timestamp=xxx", uitest.makeUniqueUrl("http://chuck-norris.com?timestamp=123456789"));
    assertEquals("http://chuck-norris.com?timestamp=xxx", uitest.makeUniqueUrl("http://chuck-norris.com?timestamp=123456789#"));
    assertEquals("http://chuck-norris.com?timestamp=xxx", uitest.makeUniqueUrl("http://chuck-norris.com?timestamp=123456789&abc=def"));
  }
}
