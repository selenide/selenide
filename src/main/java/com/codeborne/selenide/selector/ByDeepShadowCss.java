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

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNullElse;

public class ByDeepShadowCss extends By implements Serializable {

  private static final JavaScript jsSource = new JavaScript("query-selector-shadow-dom.js");

  private final String target;

  /**
   * Find target elements. It pierces Shadow DOM roots without knowing the path through nested shadow roots.
   * <p>
   * <br/> For example: #shadow-host #inner-shadow-host target-element
   * @param target CSS expression of target element
   */
  public ByDeepShadowCss(String target) {
    this.target = target;
  }

  @Override
  public WebElement findElement(SearchContext context) {
    List<WebElement> found = findElements(context);
    if (found.isEmpty()) {
      throw new NoSuchElementException("Cannot locate an element in shadow dom " + this);
    }
    return found.get(0);
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    try {
      return requireNonNullElse(jsSource.execute(context, target), emptyList());
    } catch (JavascriptException e) {
      throw new NoSuchElementException(Cleanup.of.webdriverExceptionMessage(e));
    }
  }

  @Override
  public String toString() {
    return "By.shadowDeepCss: " + target;
  }
}
