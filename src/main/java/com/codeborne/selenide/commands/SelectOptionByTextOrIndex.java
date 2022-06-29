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
    else if (args[0] instanceof String) {
      List<String> texts = merge(arguments.nth(0), arguments.nth(1));
      selectOptionsByTexts(selectField, texts);
      return null;
    }
    else if (args[0] instanceof Integer) {
      Integer index = arguments.nth(0);
      int[] otherIndexes = arguments.nth(1);
      selectOptionsByIndexes(selectField, merge(index, otherIndexes));
      return null;
    }
    else {
      throw new IllegalArgumentException("Unsupported argument (expected String or Integer): " + Arrays.toString(args));
    }
  }

  private void selectOptionsByTexts(WebElementSource selectField, List<String> texts) {
    Map<String, String> error = selectOptionByText.execute(selectField.driver(), selectField.getWebElement(), texts);
    if (error.containsKey("disabledSelect")) {
      throw new InvalidStateException(selectField.description(), "Cannot select option in a disabled select");
    }
    if (error.containsKey("disabledOption")) {
      String text = error.get("disabledOption");
      throw new InvalidStateException(selectField.description() + "/option[text:" + text + ']', "Cannot select a disabled option");
    }
    if (error.containsKey("optionNotFound")) {
      String text = error.get("optionNotFound");
      throw new ElementNotFound(selectField.getAlias(), selectField.getSearchCriteria() + "/option[text:" + text + ']', exist);
    }
  }

  private void selectOptionsByIndexes(WebElementSource selectField, List<Integer> indexes) {
    Map<String, Object> error = selectOptionByIndex.execute(selectField.driver(), selectField.getWebElement(), indexes);
    if (error.containsKey("disabledSelect")) {
      throw new InvalidStateException(selectField.description(), "Cannot select option in a disabled select");
    }
    if (error.containsKey("disabledOption")) {
      Number index = (Number) error.get("disabledOption");
      throw new InvalidStateException(selectField.description() + "/option[index:" + index + ']', "Cannot select a disabled option");
    }
    if (error.containsKey("optionNotFound")) {
      Number index = (Number) error.get("optionNotFound");
      throw new ElementNotFound(selectField.getAlias(), selectField.getSearchCriteria() + "/option[index:" + index + ']', exist);
    }
  }
}
