package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.BySelectorCollection;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class FindAll implements Command<ElementsCollection> {
  @Override
  @CheckReturnValue
  @Nonnull
  public ElementsCollection execute(SelenideElement parent, WebElementSource locator, @Nullable Object[] args) {
    Object selector = firstOf(args);
    return new ElementsCollection(
      new BySelectorCollection(locator.driver(), parent, WebElementSource.getSelector(selector))
    );
  }
}
