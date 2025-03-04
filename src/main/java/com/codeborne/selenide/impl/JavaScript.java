package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;
import static java.util.Objects.requireNonNull;
import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.DOTALL;

public class JavaScript {
  private static final Pattern RE = Pattern.compile("import '(.+?\\.js)'", DOTALL);
  private final FileContent jsSource;
  private final Lazy<String> content = lazyEvaluated(() -> readContent());

  public JavaScript(String jsFileName) {
    jsSource = new FileContent(jsFileName);
  }

  String content() {
    return content.get();
  }

  private String readContent() {
    String js = jsSource.content();
    Matcher matcher = RE.matcher(js);
    while (matcher.find()) {
      String fileName = matcher.group(1);
      String includedScript = new FileContent(fileName).content();
      js = matcher.replaceFirst(quoteReplacement(includedScript));
      matcher = RE.matcher(js);
    }
    return js;
  }

  @Nullable
  @CanIgnoreReturnValue
  @SuppressWarnings("unchecked")
  public <T> T execute(SearchContext context, @Nullable Object... arguments) {
    return (T) jsExecutor(context).executeScript("return " + content(), arguments);
  }

  @Nullable
  @CanIgnoreReturnValue
  public <T> T execute(Driver driver, Object... arguments) {
    return execute(driver.getWebDriver(), arguments);
  }

  @CanIgnoreReturnValue
  @SuppressWarnings("unchecked")
  public <T> T executeOrFail(Driver driver, Object... arguments) {
    List<@Nullable Object> result = requireNonNull(execute(driver, arguments));
    if (result.get(1) != null) {
      throw new IllegalArgumentException((String) result.get(1));
    }
    return (T) requireNonNull(result.get(0));
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
