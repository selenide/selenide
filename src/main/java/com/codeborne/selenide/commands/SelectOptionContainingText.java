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
import static com.codeborne.selenide.commands.Util.merge;

@ParametersAreNonnullByDefault
public class SelectOptionContainingText implements Command<Void> {
  private static final JavaScript selectOptionByPartialText = new JavaScript("select-options-by-partial-text.js");

  @Override
  @Nullable
  public Void execute(SelenideElement proxy, WebElementSource selectField, @Nullable Object[] args) {
    Arguments arguments = new Arguments(args);
    List<String> texts = merge(arguments.nth(0), arguments.nth(1));
    Map<String, String> error = selectOptionByPartialText.execute(selectField.driver(), selectField.getWebElement(), texts);
    if (error.containsKey("disabledSelect")) {
      throw new InvalidStateException(selectField.description(), "Cannot select option in a disabled select");
    }
    if (error.containsKey("disabledOption")) {
      String text = error.get("disabledOption");
      throw new InvalidStateException(selectField.description() + "/option[text containing:" + text + ']',
        "Cannot select a disabled option");
    }
    if (error.containsKey("optionNotFound")) {
      String text = error.get("optionNotFound");
      throw new ElementNotFound(selectField.getAlias(),
        selectField.getSearchCriteria() + "/option[text containing:" + text + ']', exist);
    }
    return null;
  }
}
