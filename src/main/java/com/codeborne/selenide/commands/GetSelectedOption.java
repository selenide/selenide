package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.support.ui.Select;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.impl.WebElementWrapper.wrap;

@ParametersAreNonnullByDefault
public class GetSelectedOption implements Command<SelenideElement> {
  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource selectElement, @Nullable Object[] args) {
    return wrap(selectElement.driver(), new Select(selectElement.getWebElement()).getFirstSelectedOption());
  }
}
