package com.codeborne.selenide;

import com.codeborne.selenide.commands.GetSelectedOptionText;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DummyGetSelectedOptionText extends GetSelectedOptionText {
  private final String text;

  public DummyGetSelectedOptionText(String text) {
    this.text = text;
  }

  @Nonnull
  @Override
  public String execute(SelenideElement proxy, WebElementSource selectElement, @Nullable Object[] args) {
    return text;
  }

  @Nonnull
  @Override
  public String execute(Driver driver, WebElement webElement) {
    return text;
  }
}
