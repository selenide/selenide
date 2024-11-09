package com.codeborne.selenide.junit;

import com.codeborne.selenide.logevents.SimpleReport;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TextReport extends TestWatcher {
  protected SimpleReport report = new SimpleReport();

  private boolean onFailedTest = true;
  private boolean onSucceededTest = true;

  @CanIgnoreReturnValue
  public TextReport onFailedTest(boolean onFailedTest) {
    this.onFailedTest = onFailedTest;
    return this;
  }

  @CanIgnoreReturnValue
  public TextReport onSucceededTest(boolean onSucceededTest) {
    this.onSucceededTest = onSucceededTest;
    return this;
  }

  @Override
  protected void starting(Description description) {
    if (onFailedTest || onSucceededTest) {
      report.start();
    }
  }

  @Override
  protected void succeeded(Description description) {
    if (onSucceededTest) {
      report.finish(description.getDisplayName());
    }
  }

  @Override
  protected void failed(Throwable e, Description description) {
    if (onFailedTest) {
      report.finish(description.getDisplayName());
    }
  }

  @Override
  protected void finished(Description description) {
    report.clean();
  }
}
