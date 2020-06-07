package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GetLastChild implements Command<SelenideElement> {

  private final Find find;

  public GetLastChild() {
    find = new Find();
  }

  public GetLastChild(Find find) {
    this.find = find;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    return find.execute(proxy, locator, By.xpath("*[last()]"), 0);
  }
}
