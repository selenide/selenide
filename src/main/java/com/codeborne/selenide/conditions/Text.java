package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

public class Text extends Condition {
  protected final String text;
  public Text(final String text) {
    super("text");
    this.text = text;
  }

  @Override
  public boolean apply(WebElement element) {
    return Html.text.contains(element.getText(), text.toLowerCase());
  }

  @Override
  public String toString() {
    return name + " '" + text + '\'';
  }
}
