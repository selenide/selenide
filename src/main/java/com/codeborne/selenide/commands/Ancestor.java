package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.ancestor.AncestorRuleEngine;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

import static com.codeborne.selenide.commands.Util.firstOf;

public class Ancestor implements Command<SelenideElement> {
  @Override
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    String selector = firstOf(args);
    int indexPredicate = args.length > 1 ?
      (args[1] instanceof Integer indexArgument ? indexArgument + 1 : 1) :
      1;

    AncestorRuleEngine ruleEngine = new AncestorRuleEngine();
    String xpath = ruleEngine.process(selector, indexPredicate).getValue();
    return locator.find(proxy, By.xpath(xpath), 0);
  }
}
