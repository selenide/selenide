package com.codeborne.selenide.selector;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class ByShadow {

  /**
   * Find target elements inside shadow-root that attached to shadow-host.
   * <br/> Supports inner shadow-hosts.
   *
   * <br/> For example: shadow-host &gt; inner-shadow-host &gt; target-element
   * (each shadow-host must be specified explicitly).
   *
   * @param target           CSS expression of target element
   * @param shadowHost       CSS expression of the shadow-host with attached shadow-root
   * @param innerShadowHosts subsequent inner shadow-hosts
   * @return A By which locates elements by CSS inside shadow-root.
   */
  @CheckReturnValue
  @Nonnull
  public static By cssSelector(String target, String shadowHost, String... innerShadowHosts) {
    return new ByShadowCss(target, shadowHost, innerShadowHosts);
  }

  @ParametersAreNonnullByDefault
  public static class ByShadowCss extends By implements Serializable {
    private final String shadowHost;
    private final String[] innerShadowHosts;
    private final String target;

    ByShadowCss(String target, String shadowHost, String... innerShadowHosts) {
      //noinspection ConstantConditions
      if (shadowHost == null || target == null) {
        throw new IllegalArgumentException("Cannot find elements when the selector is null");
      }
      this.shadowHost = shadowHost;
      this.innerShadowHosts = innerShadowHosts;
      this.target = target;
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public WebElement findElement(SearchContext context) {
      WebElement host = context.findElement(By.cssSelector(shadowHost));
      for (String innerHost : innerShadowHosts) {
        host = getElementInsideShadowTree(host, innerHost);
      }

      return getElementInsideShadowTree(host, target);
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public List<WebElement> findElements(SearchContext context) {
      WebElement host = context.findElement(By.cssSelector(shadowHost));
      for (String innerHost : innerShadowHosts) {
        host = getElementInsideShadowTree(host, innerHost);
      }

      return getElementsInsideShadowTree(host, target);
    }

    private WebElement getElementInsideShadowTree(WebElement host, String target) {
      if (isShadowRootAttached(host)) {
        WebElement targetWebElement = (WebElement) getJavascriptExecutor(host)
          .executeScript("return arguments[0].shadowRoot.querySelector(arguments[1])", host, target);
        if (targetWebElement == null) {
          throw new NoSuchElementException("The element was not found: " + target);
        }
        return targetWebElement;
      } else {
        throw new NoSuchElementException("The element is not a shadow host or has 'closed' shadow-dom mode: " + host);
      }
    }

    private boolean isShadowRootAttached(WebElement host) {
      return getJavascriptExecutor(host).executeScript("return arguments[0].shadowRoot", host) != null;
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> getElementsInsideShadowTree(WebElement host, String sh) {
      return (List<WebElement>) getJavascriptExecutor(host)
        .executeScript("return arguments[0].shadowRoot.querySelectorAll(arguments[1])", host, sh);
    }

    private JavascriptExecutor getJavascriptExecutor(SearchContext context) {
      JavascriptExecutor jsExecutor;
      if (context instanceof JavascriptExecutor) {
        jsExecutor = (JavascriptExecutor) context;
      } else {
        jsExecutor = (JavascriptExecutor) ((WrapsDriver) context).getWrappedDriver();
      }
      return jsExecutor;
    }

    @Override
    @CheckReturnValue
    @Nonnull
    public String toString() {
      StringBuilder sb = new StringBuilder("By.cssSelector: ");
      sb.append(shadowHost).append(" ");
      if (innerShadowHosts.length > 0) {
        sb.append(Arrays.toString(innerShadowHosts)).append(" ");
      }
      sb.append(target);
      return sb.toString();
    }
  }
}
