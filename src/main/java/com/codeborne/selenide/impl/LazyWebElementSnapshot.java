package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;
import java.util.List;

import static com.codeborne.selenide.impl.Lazy.lazyEvaluated;

public class LazyWebElementSnapshot extends WebElementSource {
  public static SelenideElement wrap(WebElementSource delegate) {
    return wrap(SelenideElement.class, delegate);
  }

  @SuppressWarnings("unchecked")
  public static <T extends SelenideElement> T wrap(Class<T> clazz, WebElementSource delegate) {
    return (T) Proxy.newProxyInstance(
      Thread.currentThread().getContextClassLoader(),
      new Class<?>[] {clazz},
      new SelenideElementProxy<>(new LazyWebElementSnapshot(delegate))
    );
  }

  private final WebElementSource delegate;
  private final Lazy<WebElement> snapshot;

  LazyWebElementSnapshot(WebElementSource delegate) {
    this.delegate = delegate;
    this.snapshot = lazyEvaluated(() -> delegate.getWebElement());
  }

  @Override
  public Driver driver() {
    return delegate.driver();
  }

  @Override
  public WebElement getWebElement() {
    return snapshot.get();
  }

  @Override
  public String getSearchCriteria() {
    return delegate.getSearchCriteria();
  }

  @Override
  public final void setAlias(String alias) {
    delegate.setAlias(alias);
  }

  @Override
  public Alias getAlias() {
    return delegate.getAlias();
  }

  @Override
  public String description() {
    return delegate.description();
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public SelenideElement find(SelenideElement proxy, Object arg, int index) {
    return delegate.find(proxy, arg, index);
  }

  @Override
  public List<WebElement> findAll() throws IndexOutOfBoundsException {
    return delegate.findAll();
  }

  @Override
  public ElementNotFound createElementNotFoundError(WebElementCondition condition, @Nullable Throwable cause) {
    return delegate.createElementNotFoundError(condition, cause);
  }
}
