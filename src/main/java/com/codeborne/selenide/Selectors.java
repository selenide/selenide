package com.codeborne.selenide;

import org.openqa.selenium.By;

public class Selectors {
  public static By byText(String elementText) {
    if (elementText.contains("'") && elementText.contains("\"")) {
      throw new UnsupportedOperationException("Text with both apostrophes and quotes is not supported");
    }
    return elementText.contains("'") ?
        By.xpath("//*[contains(text(), \"" + elementText + "\")]") :
        By.xpath("//*[contains(text(), '" + elementText + "')]");
  }
}
