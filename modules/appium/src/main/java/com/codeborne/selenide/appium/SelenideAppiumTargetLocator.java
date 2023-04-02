package com.codeborne.selenide.appium;

import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.ContextAware;

import java.util.Set;

public class SelenideAppiumTargetLocator {

  public void setContext(String contextName) {
    SelenideLogger.run("set context", contextName, () -> {
      (WebdriverUnwrapper.cast(WebDriverRunner.getWebDriver(), ContextAware.class))
        .map(contextAware -> contextAware.context(contextName))
        .orElseThrow(() -> new UnsupportedOperationException("Context not found" + contextName));
    });
  }

  public Set<String> getContextHandles() {
    return (WebdriverUnwrapper.cast(WebDriverRunner.getWebDriver(), ContextAware.class))
      .map(ContextAware::getContextHandles)
      .orElseThrow(() -> new UnsupportedOperationException("Cannot get contexts from mobile driver"));
  }


}
