package com.codeborne.selenide.impl;

import com.codeborne.selenide.ex.ElementIsNotClickableError;
import com.codeborne.selenide.ex.InvalidStateError;
import com.codeborne.selenide.ex.UIAssertionError;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.exist;

@ParametersAreNonnullByDefault
class ExceptionWrapper {
  @CheckReturnValue
  @Nonnull
  Throwable wrap(Throwable error, WebElementSource webElementSource) {
    if (error instanceof UIAssertionError) {
      return error;
    }
    else if (error instanceof InvalidElementStateException) {
      return new InvalidStateError(webElementSource.description(), error);
    }
    else if (isElementNotClickableException(error)) {
      return new ElementIsNotClickableError(webElementSource.description(), error);
    }
    else if (error instanceof StaleElementReferenceException || error instanceof NotFoundException) {
      return webElementSource.createElementNotFoundError(exist, error);
    }
    return error;
  }

  @CheckReturnValue
  private boolean isElementNotClickableException(Throwable e) {
    return e instanceof WebDriverException && e.getMessage().contains("is not clickable");
  }
}
