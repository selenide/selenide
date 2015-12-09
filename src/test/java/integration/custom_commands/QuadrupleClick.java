package integration.custom_commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.io.IOException;

import static integration.custom_commands.MyFramework.quadrupleClickCounter;

class QuadrupleClick implements Command<MySelenideElement> {
  @Override
  public MySelenideElement execute(SelenideElement proxy, WebElementSource locator, Object[] args) throws IOException {
    quadrupleClickCounter.incrementAndGet();
    return (MySelenideElement) proxy;
  }
}
