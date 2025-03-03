package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.JavaScript;
import com.codeborne.selenide.impl.Lists;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class ByShadow extends By implements Serializable {

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
  @CheckReturnValue
  @Nonnull
  public WebElement findElement(SearchContext context) {
    List<WebElement> found = findElements(context);
    if (found.isEmpty()) {
      throw new NoSuchElementException("Cannot locate an element " + target + " in shadow roots " + describeShadowRoots());
    }
    return found.get(0);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public List<WebElement> findElements(SearchContext context) {
    if (shadowHosts.isEmpty()) {
      return context.findElements(target);
    }

    List<SearchContext> shadowRoots = findShadowRoots(context, shadowHosts.get(0));
    for (int i = 1; i < shadowHosts.size(); i++) {
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

  @CheckReturnValue
  @Nonnull
  private static List<SearchContext> findShadowRoots(SearchContext searchContext, By shadowHost) {
    return searchContext.findElements(shadowHost)
      .stream()
      .map(ByShadow::getShadowRoot)
      .filter(Objects::nonNull)
      .toList();
  }

  @CheckReturnValue
  @Nullable
  private static SearchContext getShadowRoot(WebElement element) {
    return (SearchContext) JavaScript.jsExecutor(element).executeScript("return arguments[0].shadowRoot", element);
  }

  @Override
  public String toString() {
    return String.format("By.shadow: { %s -> %s }", describeShadowRoots(), target);
  }

  @CheckReturnValue
  @Nonnull
  private String describeShadowRoots() {
    return shadowHosts.stream()
      .map(By::toString)
      .collect(Collectors.joining(" -> "));
  }
}
