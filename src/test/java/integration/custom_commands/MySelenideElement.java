package integration.custom_commands;

import com.codeborne.selenide.SelenideElement;

public interface MySelenideElement extends SelenideElement {
  MySelenideElement tripleClick();
  MySelenideElement quadrupleClick();
}
