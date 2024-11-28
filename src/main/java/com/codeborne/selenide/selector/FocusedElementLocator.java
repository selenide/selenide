package com.codeborne.selenide.selector;

import com.codeborne.selenide.impl.JavaScript;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Subclass of `By` to find the element currently being focused
 */
public class FocusedElementLocator extends By {
  private static final JavaScript js = new JavaScript("get-focused-element.js");

  @Override
  public WebElement findElement(SearchContext context) {
    WebElement element = js.execute(context, js.node(context));
    //noinspection ConstantValue
    if (element == null) {
      throw new NoSuchElementException("Cannot find a focused element");
    }
    return element;
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    return asList(findElement(context));
  }
}
