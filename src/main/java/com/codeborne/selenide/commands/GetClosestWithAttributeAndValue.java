package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import static com.codeborne.selenide.commands.Util.firstOf;
import static java.lang.String.format;

public class GetClosestWithAttributeAndValue implements Command<SelenideElement> {

  @Override
  @Nullable
  @CheckReturnValue
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    String attributeName = firstOf(args);
    String attributeValue = (String) args[1];
    String xpath = format("ancestor::*[@%S='%s']", attributeName, attributeValue);
    return locator.find(proxy, By.xpath(xpath), 0);
  }
}
