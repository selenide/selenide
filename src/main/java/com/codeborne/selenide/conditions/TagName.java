package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class TagName extends Condition {
  private final String expectedTagName;

  public TagName(String expectedTagName) {
    super("tag name");
    this.expectedTagName = expectedTagName;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String actualTagName = element.getTagName();
    boolean matches = Objects.equals(actualTagName, expectedTagName);
    return new CheckResult(matches, String.format("tag \"%s\"", actualTagName));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("tag \"%s\"", expectedTagName);
  }
}
