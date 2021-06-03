package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class CaseSensitiveText extends Condition {
  private final String expectedText;

  public CaseSensitiveText(String expectedText) {
    super("textCaseSensitive");
    this.expectedText = expectedText;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    String elementText = "select".equalsIgnoreCase(element.getTagName()) ?
      getSelectedOptionsTexts(element) :
      element.getText();
    return Html.text.containsCaseSensitive(elementText, expectedText);
  }

  private String getSelectedOptionsTexts(WebElement element) {
    List<WebElement> selectedOptions = new Select(element).getAllSelectedOptions();
    StringBuilder sb = new StringBuilder();
    for (WebElement selectedOption : selectedOptions) {
      sb.append(selectedOption.getText());
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return String.format("%s '%s'", getName(), expectedText);
  }
}
