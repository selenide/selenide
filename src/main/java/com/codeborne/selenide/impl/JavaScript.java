package com.codeborne.selenide.impl;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JavaScript {
  private final FileContent jsSource;

  public JavaScript(String jsFileName) {
    jsSource = new FileContent(jsFileName);
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public <T> T execute(SearchContext context, Object... arguments) {
    String js = "return " + jsSource.content() + "(arguments[0], arguments[1])";
    return (T) jsExecutor(context).executeScript(js, arguments);
  }

  private JavascriptExecutor jsExecutor(SearchContext context) {
    if (context instanceof JavascriptExecutor) {
      return (JavascriptExecutor) context;
    }
    else if (context instanceof WrapsDriver) {
      return (JavascriptExecutor) ((WrapsDriver) context).getWrappedDriver();
    }
    else {
      throw new IllegalArgumentException("Context is not JS-aware: " + context);
    }
  }

  @Nullable
  public Object node(SearchContext context) {
    return context instanceof WebDriver ? null : context;
  }
}
