package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface WebElementsCollection {
  /**
   * get elements of this collection (probably cached).
   */
  @CheckReturnValue
  @Nonnull
  List<WebElement> getElements();

  @CheckReturnValue
  @Nonnull
  String description();

  @CheckReturnValue
  @Nonnull
  Driver driver();
}
