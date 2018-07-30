package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.By;

public class GetLastChild implements Command<SelenideElement> {

  private Find find;

  public GetLastChild() {
    find = new Find();
  }

  public GetLastChild(Find find) {
    this.find = find;
  }

  @Override
  public SelenideElement execute(final SelenideElement proxy, final WebElementSource locator, final Object[] args) {
    return find.execute(proxy, locator, By.xpath("*[last()]"), 0);
  }
}
