package com.codeborne.selenide.selector;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.io.Serializable;
import java.util.List;

public class ByDeepShadow extends By implements Serializable {

  private final By target;

  public ByDeepShadow(By target) {
    //noinspection ConstantConditions
    if (target == null) {
      throw new IllegalArgumentException("Cannot find elements when the selector is null");
    }
    this.target = target;
  }

  @Override
  public WebElement findElement(SearchContext context) {
    List<WebElement> elements = findElements(context, target, false);
    if (elements.isEmpty()) {
      throw new NoSuchElementException("Cannot locate an element in shadow dom " + this);
    }
    return elements.get(0);
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    return findElements(context, target, true);
  }

  private static List<WebElement> findElements(SearchContext context, By target, boolean findAll) {
    List<WebElement> result = context.findElements(target);
    if (!result.isEmpty() && !findAll) {
      return result;
    }

    List<SearchContext> shadowRoots = findShadowRoots(context);
    for (SearchContext shadowRoot : shadowRoots) {
      List<WebElement> elements = findElements(shadowRoot, target, findAll);
      if (!elements.isEmpty()) {
        if (!findAll) {
          return elements;
        }
        result.addAll(elements);
      }
    }
    return result;
  }

  private static List<SearchContext> findShadowRoots(SearchContext context) {
    // an attempt to find All the Shadow Roots under the giving context
    return ByShadow.findShadowRoots(context, By.cssSelector("*"));
  }

  @Override
  public String toString() {
    return "By.shadowDeep: " + target;
  }
}
