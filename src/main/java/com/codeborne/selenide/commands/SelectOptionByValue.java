package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import com.codeborne.selenide.impl.Arguments;
import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.WebElementSource;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.commands.Util.arrayToString;
import static com.codeborne.selenide.commands.Util.cast;

@ParametersAreNonnullByDefault
public class SelectOptionByValue implements Command<Void> {
  private static final JavaScript selectOptionByValue = new JavaScript("select-options-by-value.js");

  @Override
  @Nullable
  public Void execute(SelenideElement proxy, WebElementSource selectField, @Nullable Object[] args) {
    Arguments arguments = new Arguments(args);
    List<String> values = Util.merge(arguments.nth(0), arguments.nth(1));
    selectOptionByValue(selectField, values);
    return null;
  }

  private void selectOptionByValue(WebElementSource selectField, List<String> values) {
    Map<String, String> error = selectOptionByValue.execute(selectField.driver(), selectField.getWebElement(), values);
    if (error.containsKey("nonSelect")) {
      throw new IllegalArgumentException("Cannot select option from a non-select element");
    }
    if (error.containsKey("disabledSelect")) {
      throw new InvalidStateException(selectField.driver(), selectField.description(), "Cannot select option in a disabled select");
    }
    if (error.containsKey("disabledOptions")) {
      List<String> value = cast(error.get("disabledOptions"));
      String elementDescription = String.format("%s/option[value:%s]", selectField.description(), arrayToString(value));
      throw new InvalidStateException(selectField.driver(), elementDescription, "Cannot select a disabled option");
    }
    if (error.containsKey("optionsNotFound")) {
      List<String> value = cast(error.get("optionsNotFound"));
      String elementDescription = String.format("%s/option[value:%s]", selectField.getSearchCriteria(), arrayToString(value));
      throw new ElementNotFound(selectField.driver(), selectField.getAlias(), elementDescription, exist);
    }
  }
}
