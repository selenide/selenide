package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collection;

import static java.lang.System.lineSeparator;

public interface ElementDescriber {
  String fully(Driver driver, @Nullable WebElement element);

  String briefly(Driver driver, WebElement element);

  String selector(By selector);

  /**
   * Outputs string presentation of the element's collection
   *
   * @param elements elements of string
   * @return e.g. {@code "[<h1>foo</h1>, <h2>bar</h2>]"}
   * @see <a href="https://github.com/selenide/selenide/wiki/do-not-use-getters-in-tests">NOT RECOMMENDED</a>
   */
  default String fully(Driver driver, @Nullable Collection<WebElement> elements) {
    if (elements == null) {
      return "[not loaded yet...]";
    }

    if (elements.isEmpty()) {
      return "[]";
    }

    StringBuilder sb = new StringBuilder(256);
    sb.append("[").append(lineSeparator()).append("\t");
    for (WebElement element : elements) {
      if (sb.length() > 4) {
        sb.append(",").append(lineSeparator()).append("\t");
      }
      sb.append(fully(driver, element));
    }
    sb.append(lineSeparator()).append("]");
    return sb.toString();
  }
}
