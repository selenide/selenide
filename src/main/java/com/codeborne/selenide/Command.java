package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementSource;

import java.io.IOException;

public interface Command<T> {
  Object[] NO_ARGS = new Object[0];

  T execute(SelenideElement proxy, WebElementSource locator, Object[] args) throws IOException;
}
