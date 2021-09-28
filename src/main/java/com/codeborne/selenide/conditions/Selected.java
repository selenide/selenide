package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Selected extends Condition {

  public Selected() {
    super("selected");
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    boolean selected = element.isSelected();
    return new CheckResult(selected, selected ? "selected" : "not selected");
  }
}
