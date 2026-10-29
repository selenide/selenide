package com.codeborne.selenide.proxy;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Proxy;

import java.util.Objects;

/**
 * Selenium proxy pointing to Selenide proxy server.
 * <p>
 * Remembers the outside proxy (e.g. a corporate proxy) that Selenide proxy server is chained to:
 * Selenium Manager should use the outside proxy directly instead of Selenide proxy server.
 */
public class SelenideSeleniumProxy extends Proxy {
  private final @Nullable Proxy outsideProxy;

  public SelenideSeleniumProxy(Proxy seleniumProxy, @Nullable Proxy outsideProxy) {
    super(seleniumProxy.toJson());
    this.outsideProxy = outsideProxy;
  }

  public @Nullable Proxy outsideProxy() {
    return outsideProxy;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    return super.equals(o) && Objects.equals(outsideProxy, ((SelenideSeleniumProxy) o).outsideProxy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), outsideProxy);
  }
}
