package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class JavaScript {
  private final FileContent jsSource;

  public JavaScript(String jsFileName) {
    jsSource = new FileContent(jsFileName);
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public <T> T execute(SearchContext context, Object... arguments) {
    String js = "return " + jsSource.content();
    return (T) jsExecutor(context).executeScript(js, arguments);
  }

  @Nonnull
  public <T> T execute(Driver driver, Object... arguments) {
    return execute(driver.getWebDriver(), arguments);
  }

  @Nonnull
  @CheckReturnValue
  @SuppressWarnings("unchecked")
  public <T> T executeOrFail(Driver driver, Object... arguments) {
    List<Object> result = execute(driver, arguments);
    if (result.get(1) != null) {
      throw new IllegalArgumentException((String) result.get(1));
    }
    return (T) result.get(0);
  }

  public static JavascriptExecutor jsExecutor(SearchContext context) {
    return asJsExecutor(context)
      .orElseThrow(() -> new IllegalArgumentException("Context is not JS-aware: " + context));
  }

  public static Optional<JavascriptExecutor> asJsExecutor(SearchContext context) {
    SearchContext unwrappedContext = context instanceof WrapsDriver wrapsDriver ?
      wrapsDriver.getWrappedDriver() : context;

    return unwrappedContext instanceof JavascriptExecutor jsExecutor ?
      Optional.of(jsExecutor) :
      Optional.empty();
  }

  @Nullable
  public Object node(SearchContext context) {
    return context instanceof WebDriver ? null : context;
  }
}
