package com.codeborne.selenide.junit;

import com.codeborne.selenide.logevents.ErrorsCollector;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SoftAsserts extends ExternalResource {
  private Description currentTest;
  private final ErrorsCollector errorsCollector = new ErrorsCollector();

  public SoftAsserts() {
    SelenideLogger.addListener("softAssert", errorsCollector);
  }

  @Override
  public Statement apply(Statement base, Description description) {
    currentTest = description;
    return super.apply(base, description);
  }

  @Override
  protected void before() throws Throwable {
    errorsCollector.clear();
  }

  @Override
  protected void after() {
    errorsCollector.failIfErrors(currentTest.getDisplayName());
  }
}
