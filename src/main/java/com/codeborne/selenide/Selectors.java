package com.codeborne.selenide;

import org.openqa.selenium.By;

public class Selectors {
  /**
   * Find element CONTAINING given text (as a substring)
   * @param elementText Text to search inside element
   * @return standard selenium By criteria
   */
  public static By withText(String elementText) {
    if (elementText.contains("'") && elementText.contains("\"")) {
      throw new UnsupportedOperationException("Text with both apostrophes and quotes is not supported");
    }
    return elementText.contains("'") ?
        By.xpath(".//*[contains(normalize-space(text()), \"" + elementText + "\")]") :
        By.xpath(".//*[contains(normalize-space(text()), '" + elementText + "')]");
  }

  /**
   * Find element HAVING given text (exactly this text)
   * @param elementText Text that searched element should have
   * @return standard selenium By criteria
   */
  public static By byText(String elementText) {
    if (elementText.contains("'") && elementText.contains("\"")) {
      throw new UnsupportedOperationException("Text with both apostrophes and quotes is not supported");
    }
    return elementText.contains("'") ?
        By.xpath(".//*[normalize-space(text()) = \"" + elementText + "\"]") :
        By.xpath(".//*[normalize-space(text()) = '" + elementText + "']");
  }
}