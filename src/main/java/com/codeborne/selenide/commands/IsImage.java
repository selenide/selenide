package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

public class IsImage implements Command<Boolean> {
  @Override
  public Boolean execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    WebElement img = locator.getWebElement();
    if (!"img".equalsIgnoreCase(img.getTagName())) {
      throw new IllegalArgumentException("Method isImage() is only applicable for img elements");
    }
    return locator.driver().executeJavaScript("return arguments[0].complete && " +
        "typeof arguments[0].naturalWidth != 'undefined' && " +
        "arguments[0].naturalWidth > 0", img);
  }
}
