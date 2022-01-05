package com.codeborne.selenide.logevents;

import com.codeborne.selenide.Configuration;

import static com.codeborne.selenide.AssertionMode.SOFT;

public class SoftAssertsErrorsCollector extends ErrorsCollector {
  @Override
  protected boolean isEnabled() {
    return Configuration.assertionMode == SOFT;
  }
}
