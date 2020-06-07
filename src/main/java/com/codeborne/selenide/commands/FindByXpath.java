package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class FindByXpath implements Command<SelenideElement> {

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object... args) {
    String xpath = firstOf(args);

    By byXpath = By.xpath(xpath);
    return args.length == 1 ?
        locator.find(proxy, byXpath, 0) :
        locator.find(proxy, byXpath, (Integer) args[1]);
  }

}
