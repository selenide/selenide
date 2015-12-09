package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.open;

public class FollowLink implements Command<Void> {
  Click click = new Click();
  
  @Override
  public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    WebElement link = locator.getWebElement();
    String href = link.getAttribute("href");
    click.execute(proxy, locator, args);

    // JavaScript $.click() doesn't take effect for <a href>
    if (href != null) {
      open(href);
    }
    return null;
  }
}
