package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.conditions.IsImageLoaded;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IsImage implements Command<Boolean> {
  @Override
  @CheckReturnValue
  public Boolean execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    WebElement img = locator.getWebElement();
    if (!"img".equalsIgnoreCase(img.getTagName())) {
      throw new IllegalArgumentException("Method isImage() is only applicable for img elements");
    }
    return IsImageLoaded.isImage(locator.driver(), img);
  }
}
