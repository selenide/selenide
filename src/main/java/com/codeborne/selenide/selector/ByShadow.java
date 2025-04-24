package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class ByShadow extends By implements Serializable {

  private static final String GET_SHADOW_ROOT_SCRIPT = "return arguments[0].shadowRoot";

  private final By target;
  private final List<By> shadowHosts;

  public ByShadow(By target, By shadowHost, By... innerShadowHosts) {
    //noinspection ConstantConditions
    if (target == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null");
    }
    this.target = target;
    this.shadowHosts = Lists.list(shadowHost, innerShadowHosts);
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
    if (shadowHosts.isEmpty()) {
      return context.findElements(target);
    }

    List<SearchContext> shadowRoots = findShadowRoots(context, shadowHosts.get(0));
    int shadowHostCount = shadowHosts.size();
    for (int i = 1; i < shadowHostCount; i++) {
      By shadowHost = shadowHosts.get(i);

      shadowRoots = shadowRoots.stream()
        .map(shadowRoot -> findShadowRoots(shadowRoot, shadowHost))
        .flatMap(Collection::stream)
        .toList();
    }

    return shadowRoots.stream()
      .map(shadowRoot -> shadowRoot.findElements(target))
      .flatMap(Collection::stream)
      .toList();
  }

  private static List<SearchContext> findShadowRoots(SearchContext searchContext, By shadowHost) {
    return searchContext.findElements(shadowHost)
      .stream()
      .map(ByShadow::getShadowRoot)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .toList();
  }

  /**
   * Returns a shadow root of the given element if it presents.
   *
   * @param element element whose shadow root has to be obtained
   * @return shadow root context if it presents
   * @see ByShadow#GET_SHADOW_ROOT_SCRIPT
   */
  public static Optional<SearchContext> getShadowRoot(WebElement element) {
    JavascriptExecutor executor = JavaScript.jsExecutor(element);
    SearchContext shadowRoot = (SearchContext) executor.executeScript(GET_SHADOW_ROOT_SCRIPT, element);
    return Optional.ofNullable(shadowRoot);
  }

  @Override
  public String toString() {
    return String.format("By.shadow: { %s -> %s }", describeShadowRoots(), target);
  }

  private String describeShadowRoots() {
    return shadowHosts.stream()
      .map(By::toString)
      .collect(joining(" -> "));
  }

  public static By cssSelector(String target, String shadowHost, String... innerShadowHosts) {
    return ByShadowCss.cssSelector(target, shadowHost, innerShadowHosts);
  }
}
