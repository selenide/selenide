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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.commands.Util.arrayToString;
import static com.codeborne.selenide.commands.Util.cast;
import static com.codeborne.selenide.commands.Util.merge;

@ParametersAreNonnullByDefault
public class SelectOptionByTextOrIndex implements Command<Void> {
  private static final JavaScript selectOptionByIndex = new JavaScript("select-options-by-index.js");
  private static final JavaScript selectOptionByText = new JavaScript("select-options-by-text.js");

  @Override
  @Nullable
  public Void execute(SelenideElement proxy, WebElementSource selectField, @Nullable Object[] args) {
    Arguments arguments = new Arguments(args);
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Missing arguments");
    }
    else if (args[0] instanceof String firstOptionText) {
      List<String> texts = merge(firstOptionText, arguments.nth(1));
      selectOptionsByTexts(selectField, texts);
      return null;
    }
    else if (args[0] instanceof Integer firstOptionIndex) {
      int[] otherIndexes = arguments.nth(1);
      selectOptionsByIndexes(selectField, merge(firstOptionIndex, otherIndexes));
      return null;
    }
    else {
      throw new IllegalArgumentException("Unsupported argument (expected String or Integer): " + Arrays.toString(args));
    }
  }

  private void selectOptionsByTexts(WebElementSource selectField, List<String> texts) {
    Map<String, String> error = selectOptionByText.execute(selectField.driver(), selectField.getWebElement(), texts);
    if (error.containsKey("nonSelect")) {
      throw new IllegalArgumentException("Cannot select option from a non-select element");
    }
    if (error.containsKey("disabledSelect")) {
      throw new InvalidStateException(selectField.driver(), selectField.description(), "Cannot select option in a disabled select");
    }
    if (error.containsKey("disabledOptions")) {
      List<String> optionsTexts = cast(error.get("disabledOptions"));
      String elementDescription = String.format("%s/option[text:%s]", selectField.description(), arrayToString(optionsTexts));
      throw new InvalidStateException(selectField.driver(), elementDescription, "Cannot select a disabled option");
    }
    if (error.containsKey("optionsNotFound")) {
      List<String> optionsTexts = cast(error.get("optionsNotFound"));
      String elementDescription = String.format("%s/option[text:%s]", selectField.getSearchCriteria(), arrayToString(optionsTexts));
      throw new ElementNotFound(selectField.driver(), selectField.getAlias(), elementDescription, exist);
    }
  }

  private void selectOptionsByIndexes(WebElementSource selectField, List<Integer> indexes) {
    Map<String, Object> error = selectOptionByIndex.execute(selectField.driver(), selectField.getWebElement(), indexes);
    if (error.containsKey("nonSelect")) {
      throw new IllegalArgumentException("Cannot select option from a non-select element");
    }
    if (error.containsKey("disabledSelect")) {
      throw new InvalidStateException(selectField.driver(), selectField.description(), "Cannot select option in a disabled select");
    }
    if (error.containsKey("disabledOptions")) {
      List<Integer> index = cast(error.get("disabledOptions"));
      String elementDescription = String.format("%s/option[index:%s]", selectField.description(), arrayToString(index));
      throw new InvalidStateException(selectField.driver(), elementDescription, "Cannot select a disabled option");
    }
    if (error.containsKey("optionsNotFound")) {
      List<Integer> index = cast(error.get("optionsNotFound"));
      String elementDescription = String.format("%s/option[index:%s]", selectField.getSearchCriteria(), arrayToString(index));
      throw new ElementNotFound(selectField.driver(), selectField.getAlias(), elementDescription, exist);
    }
  }
}
