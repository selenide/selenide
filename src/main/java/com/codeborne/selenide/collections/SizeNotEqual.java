package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SizeNotEqual extends CollectionCondition {
  protected final int expectedSize;

  public SizeNotEqual(int expectedSize) {
    this.expectedSize = expectedSize;
  }

  @Override
  public boolean apply(List<WebElement> elements) {
    return apply(elements.size());
  }

  @Override
  public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
    throw new ListSizeMismatch(collection.driver(), "<>", expectedSize, explanation, collection, elements, lastError, timeoutMs);
  }

  @Override
  public boolean applyNull() {
    return apply(0);
  }

  @Override
  public String toString() {
    return String.format("size <> %s", expectedSize);
  }

  private boolean apply(int size) {
    return size != expectedSize;
  }
}
