package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.FailFastCondition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;

import java.io.IOException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class FailFast implements Command<SelenideElement> {

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideElement execute(SelenideElement proxy, WebElementSource locator, @Nullable Object[] args) throws IOException {
    locator.setFailFast((FailFastCondition) requireNonNull(args)[0]);
    return proxy;
  }
}
