package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.Plugins;
import com.codeborne.selenide.impl.ElementDescriber;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriverException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ToString implements Command<String> {
  private final ElementDescriber describe = Plugins.getElementDescriber();

  @Override
  @CheckReturnValue
  @Nonnull
  public String execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
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
