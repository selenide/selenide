package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.closest.ClosestRuleEngine;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class GetClosest implements Command<SelenideElement> {
  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    String selector = firstOf(args);
    ClosestRuleEngine ruleEngine = new ClosestRuleEngine();
    String xpath = ruleEngine.process(selector).getValue();
    return locator.find(proxy, By.xpath(xpath), 0);
  }
}
