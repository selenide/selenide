package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.commands.Util.firstOf;

@ParametersAreNonnullByDefault
public class Matches implements Command<Boolean> {
  @Override
  @CheckReturnValue
  public Boolean execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    Condition condition = firstOf(args);
    WebElement element = getElementOrNull(locator);
    if (element != null) {
      return condition.apply(locator.driver(), element);
    }

    return condition.applyNull();
  }

  @CheckReturnValue
  @Nullable
  protected WebElement getElementOrNull(WebElementSource locator) {
    try {
      return locator.getWebElement();
    } catch (WebDriverException | ElementNotFound elementNotFound) {
      if (Cleanup.of.isInvalidSelectorError(elementNotFound))
        throw Cleanup.of.wrap(elementNotFound);
      return null;
    } catch (IndexOutOfBoundsException ignore) {
      return null;
    } catch (RuntimeException e) {
      throw Cleanup.of.wrap(e);
    }
  }
}
