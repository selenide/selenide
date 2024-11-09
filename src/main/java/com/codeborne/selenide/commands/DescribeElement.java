package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.ElementDescriber;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriverException;

import static com.codeborne.selenide.impl.Plugins.inject;

public class DescribeElement implements Command<String> {
  private final ElementDescriber describe = inject(ElementDescriber.class);

  @Override
  @SuppressWarnings("ErrorNotRethrown")
  public String execute(SelenideElement proxy, WebElementSource locator, Object @Nullable [] args) {
    try {
      return describe.fully(locator.driver(), locator.getWebElement());
    } catch (WebDriverException elementDoesNotExist) {
      return Cleanup.of.webdriverExceptionMessage(elementDoesNotExist);
    } catch (ElementNotFound elementDoesNotExist) {
      return elementDoesNotExist.getMessage();
    } catch (IndexOutOfBoundsException elementDoesNotExist) {
      return elementDoesNotExist.toString();
    }
  }
}
