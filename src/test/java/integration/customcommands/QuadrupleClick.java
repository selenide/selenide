package integration.customcommands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import static integration.customcommands.MyFramework.quadrupleClickCounter;

class QuadrupleClick implements Command<MySelenideElement> {
  @Override
  public MySelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
    quadrupleClickCounter.incrementAndGet();
    return (MySelenideElement) proxy;
  }
}
