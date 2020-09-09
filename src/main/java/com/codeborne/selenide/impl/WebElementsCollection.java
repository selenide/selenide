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

  /**
   * get Nth element of this collection
   */
  @CheckReturnValue
  @Nonnull
  WebElement getElement(int index);

  @CheckReturnValue
  @Nonnull
  String description();

  @CheckReturnValue
  @Nonnull
  Driver driver();
}
