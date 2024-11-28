package com.codeborne.selenide;

import com.codeborne.selenide.commands.GetSelectedOptionText;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

public class DummyGetSelectedOptionText extends GetSelectedOptionText {
  private final String text;

  public DummyGetSelectedOptionText(String text) {
    this.text = text;
  }

  @Override
  public String execute(SelenideElement proxy, WebElementSource selectElement, Object @Nullable [] args) {
    return text;
  }

  @Override
  public String execute(Driver driver, WebElement webElement) {
    return text;
  }
}
