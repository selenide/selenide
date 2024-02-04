package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateError;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.commands.Util.firstOf;
import static com.codeborne.selenide.impl.WebElementWrapper.wrap;

@ParametersAreNonnullByDefault
public class SelectRadio implements Command<SelenideElement> {
  private final Click click;

  public SelectRadio() {
    this(new Click());
  }

  SelectRadio(Click click) {
    this.click = click;
  }

  @Override
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) {
    String value = firstOf(args);
    List<WebElement> matchingRadioButtons = locator.findAll();
    for (WebElement radio : matchingRadioButtons) {
      if (value.equals(radio.getAttribute("value"))) {
        if (radio.getAttribute("readonly") != null) {
          throw new InvalidStateError(locator.driver(), locator.description(), "Cannot select readonly radio button");
        }

        click.click(locator.driver(), radio, usingDefaultMethod());
        return wrap(locator.driver(), radio, locator.getSearchCriteria());
      }
    }
    throw new ElementNotFound(locator.driver(), locator.getAlias(), locator.getSearchCriteria(), value(value));
  }
}
