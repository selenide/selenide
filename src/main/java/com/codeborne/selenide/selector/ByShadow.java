package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.io.Serializable;
import java.util.List;

import static com.codeborne.selenide.impl.Lists.list;
import static java.util.Objects.requireNonNull;

public class ByShadow {
  private static final JavaScript jsSource = new JavaScript("find-in-shadow-roots.js");

  /**
   * Find target elements inside shadow-root that attached to shadow-host.
   * <br/> Supports inner shadow-hosts.
   * <p>
   * <br/> For example: shadow-host &gt; inner-shadow-host &gt; target-element
   * (each shadow-host must be specified explicitly).
   *
   * @param target           CSS expression of target element
   * @param shadowHost       CSS expression of the shadow-host with attached shadow-root
   * @param innerShadowHosts subsequent inner shadow-hosts
   * @return A By which locates elements by CSS inside shadow-root.
   */
  public static By cssSelector(String target, String shadowHost, String... innerShadowHosts) {
    return new ByShadowCss(target, shadowHost, innerShadowHosts);
  }

  public static class ByShadowCss extends By implements Serializable {
    private final List<String> shadowHostsChain;
    private final String target;

    ByShadowCss(String target, String shadowHost, String... innerShadowHosts) {
      //noinspection ConstantConditions
      if (shadowHost == null || target == null) {
        throw new IllegalArgumentException("Cannot find elements when the selector is null");
      }
      shadowHostsChain = list(shadowHost, innerShadowHosts);
      this.target = target;
    }

    @Override
    public WebElement findElement(SearchContext context) {
      List<WebElement> found = findElements(context);
      if (found.isEmpty()) {
        throw new NoSuchElementException("Cannot locate an element " + target + " in shadow roots " + describeShadowRoots());
      }
      return found.get(0);
    }

    @Override
    public List<WebElement> findElements(SearchContext context) {
      try {
        if (context instanceof WebElement) {
          return requireNonNull(jsSource.execute(context, target, shadowHostsChain, context));
        }
        else {
          return requireNonNull(jsSource.execute(context, target, shadowHostsChain));
        }
      }
      catch (JavascriptException e) {
        throw new NoSuchElementException(Cleanup.of.webdriverExceptionMessage(e));
      }
    }

    @Override
    public String toString() {
      return "By.cssSelector: " + describeShadowRoots() + " -> " + target;
    }

    private String describeShadowRoots() {
      return String.join(" -> ", shadowHostsChain);
    }
  }
}
