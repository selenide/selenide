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
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNullElse;

public class ByShadowCss extends By implements Serializable {

  private static final JavaScript jsSource = new JavaScript("find-in-shadow-roots.js");

  private final List<String> shadowHostsChain;
  private final String target;

  public ByShadowCss(String target, String shadowHost, String... innerShadowHosts) {
    //noinspection ConstantConditions
    if (shadowHost == null || target == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null");
    }
    this.shadowHostsChain = list(shadowHost, innerShadowHosts);
    this.target = target;
  }


  public static By cssSelector(String target, String shadowHost, String... innerShadowHosts) {
    return new ByShadowCss(target, shadowHost, innerShadowHosts);
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
        return requireNonNullElse(jsSource.execute(context, target, shadowHostsChain, context), emptyList());
      } else {
        return requireNonNullElse(jsSource.execute(context, target, shadowHostsChain), emptyList());
      }
    } catch (JavascriptException e) {
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
