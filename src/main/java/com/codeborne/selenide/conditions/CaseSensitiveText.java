package com.codeborne.selenide.conditions;

import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static java.util.stream.Collectors.joining;

@ParametersAreNonnullByDefault
public class CaseSensitiveText extends TextCondition {

  public CaseSensitiveText(String expectedText) {
    super("text case sensitive", expectedText);
  }

  @Override
  protected boolean match(String actualText, String expectedText) {
    return Html.text.containsCaseSensitive(actualText, expectedText);
  }

  @Nullable
  @Override
  protected String getText(WebElement element) {
    return "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionsTexts(element) :
      element.getText();
  }

  private String getSelectedOptionsTexts(WebElement element) {
    List<WebElement> selectedOptions = new Select(element).getAllSelectedOptions();
    return selectedOptions.stream().map(WebElement::getText).collect(joining());
  }
}
