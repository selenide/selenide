package com.codeborne.selenide;

import org.openqa.selenium.By;

public class Selectors {
  public static By byText(String elementText) {
    return By.xpath("//*[contains(text(), '" + elementText + "')]");
  }
}
