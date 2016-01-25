package com.codeborne.selenide.collections;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ListSize extends CollectionCondition {
  protected final int expectedSize;
  protected final Operator operator;

  public ListSize(int expectedSize) {
    this.expectedSize = expectedSize;
    this.operator = Operator.EQUALS;
  }

  public ListSize(Operator operator, int expectedSize) {
    this.expectedSize = expectedSize;
    this.operator = operator;
  }

  @Override
  public boolean apply(List<WebElement> elements) {
    switch (this.operator) {
      case EQUALS: return elements.size() == expectedSize;
      case GREATER_OR_EQUALS: return elements.size() >= expectedSize;
      case GREATER: return elements.size() > expectedSize;
      case LESS_OR_EQUALS: return elements.size() <= expectedSize;
      case LESS: return elements.size() < expectedSize;
      case NOT_EQUAL: return elements.size() != expectedSize;
      default: return elements.size() == expectedSize;
    }
  }

  @Override
  public void fail(WebElementsCollection collection, List<WebElement> elements, Exception lastError, long timeoutMs) {
    throw new ListSizeMismatch(operator, expectedSize, collection, elements, lastError, timeoutMs);
  }

  @Override
  public String toString() {
    return String.format("size(%s)", expectedSize);
  }

  /**
   * Comparison operators for size of collections.
   */
  public enum Operator {
    /**
     * Math sign: =
     */
    EQUALS("="),
    /**
     * Math sign: >=
     */
    GREATER_OR_EQUALS(">="),
    /**
     * Math sign: >
     */
    GREATER(">"),
    /**
     * Math sign: <=
     */
    LESS_OR_EQUALS("<="),
    /**
     * Math sign: <
     */
    LESS("<"),
    /**
     * Math sign: !=
     */
    NOT_EQUAL("!=");

    public final String sign;

    Operator(String sign){
      this.sign = sign;
    }
  }
}
