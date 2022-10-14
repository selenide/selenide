package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.commands.GetSelectedOptionText;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@ParametersAreNonnullByDefault
public class Text extends CaseInsensitiveTextCondition {

  private final GetSelectedOptionText getSelectedOptionsTexts;

  public Text(String text) {
    this(text, new GetSelectedOptionText());
  }

  Text(final String text, GetSelectedOptionText getSelectedOptionsTexts) {
    super("text", text);

    if (isEmpty(text)) {
      throw new IllegalArgumentException("Argument must not be null or empty string. " +
        "Use $.shouldBe(empty) or $.shouldHave(exactText(\"\").");
    }
    this.getSelectedOptionsTexts = getSelectedOptionsTexts;
  }

  @Nullable
  @CheckReturnValue
  @Override
  protected String getText(Driver driver, WebElement element) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionsTexts.execute(driver, element) :
      element.getText();
  }
}
