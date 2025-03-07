package com.codeborne.selenide.testng;

import org.testng.ITestContext;
import org.testng.reporters.ExitCodeListener;

import static com.codeborne.selenide.Selenide.closeWebDriver;

/**
 * Annotate your test class with {@code @Listeners({ BrowserPerClass.class})}
 */
public class BrowserPerClass extends ExitCodeListener {
  @Override
  public void onFinish(ITestContext context) {
    super.onFinish(context);
    closeWebDriver();
  }
}
