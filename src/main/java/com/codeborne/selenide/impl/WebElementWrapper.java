package com.codeborne.selenide.impl;

import com.codeborne.selenide.Context;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Proxy;

public class WebElementWrapper extends WebElementSource {
  public static SelenideElement wrap(Context context, WebElement element) {
    return element instanceof SelenideElement ?
        (SelenideElement) element :
        (SelenideElement) Proxy.newProxyInstance(
            element.getClass().getClassLoader(), new Class<?>[]{SelenideElement.class},
            new SelenideElementProxy(new WebElementWrapper(context, element)));
  }

  private final Context context;
  private final WebElement delegate;

  protected WebElementWrapper(Context context, WebElement delegate) {
    this.context = context;
    this.delegate = delegate;
  }

  @Override
  public WebElement getWebElement() {
    return delegate;
  }

  @Override
  public String getSearchCriteria() {
    return Describe.shortly(context, delegate);
  }

  @Override
  public String toString() {
    return Describe.describe(context(), delegate);
  }

  @Override
  public Context context() {
    return context;
  }
}
