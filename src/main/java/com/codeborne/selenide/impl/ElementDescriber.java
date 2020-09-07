package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ElementDescriber {
  @CheckReturnValue
  @Nonnull
  String fully(Driver driver, @Nullable WebElement element);

  @CheckReturnValue
  @Nonnull
  String briefly(Driver driver, @Nonnull WebElement element);

  @CheckReturnValue
  @Nonnull
  String selector(By selector);
}
