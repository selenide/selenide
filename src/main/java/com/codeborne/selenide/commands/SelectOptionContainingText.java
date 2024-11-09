package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateError;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.commands.Util.arrayToString;
import static com.codeborne.selenide.commands.Util.cast;
import static com.codeborne.selenide.commands.Util.merge;
import static java.util.Objects.requireNonNull;

public class SelectOptionContainingText implements Command<Void> {
  private static final JavaScript selectOptionByPartialText = new JavaScript("select-options-by-partial-text.js");

  @Override
  @Nullable
  public Void execute(SelenideElement proxy, WebElementSource selectField, Object @Nullable [] args) {
    Arguments arguments = new Arguments(args);
    List<String> texts = merge(requireNonNull(arguments.nth(0)), requireNonNull(arguments.nth(1)));
    Driver driver = selectField.driver();
    WebElement webElement = selectField.getWebElement();
    Map<String, String> error = requireNonNull(selectOptionByPartialText.execute(driver, webElement, texts));
    if (error.containsKey("nonSelect")) {
      throw new IllegalArgumentException("Cannot select option from a non-select element");
    }
    if (error.containsKey("disabledSelect")) {
      throw new InvalidStateError(selectField.description(), "Cannot select option in a disabled select");
    }
    if (error.containsKey("disabledOptions")) {
      List<String> text = cast(error.get("disabledOptions"));
      String elementDescription = String.format("%s/option[text containing:%s]", selectField.description(), arrayToString(text));
      throw new InvalidStateError(elementDescription, "Cannot select a disabled option");
    }
    if (error.containsKey("optionsNotFound")) {
      List<String> text = cast(error.get("optionsNotFound"));
      String elementDescription = String.format("%s/option[text containing:%s]", selectField.getSearchCriteria(), arrayToString(text));
      throw new ElementNotFound(selectField.getAlias(), elementDescription, exist);
    }
    return null;
  }
}
