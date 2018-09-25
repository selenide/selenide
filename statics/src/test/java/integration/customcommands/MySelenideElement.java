package integration.customcommands;

import com.codeborne.selenide.SelenideElement;

public interface MySelenideElement extends SelenideElement {
  MySelenideElement tripleClick();

  MySelenideElement quadrupleClick();
}
