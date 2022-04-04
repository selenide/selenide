package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.OutputType;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public interface Photographer {
  @Nonnull
  @CheckReturnValue
  <T> Optional<T> takeScreenshot(Driver driver, OutputType<T> outputType);
}
