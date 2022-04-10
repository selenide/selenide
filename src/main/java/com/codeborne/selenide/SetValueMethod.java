package com.codeborne.selenide;

public enum SetValueMethod {
  /**
   * Set value by sending keyboard events (built-in WebDriver method "sendKeys").
   * Pros:
   *   it's the "natural way" - users usually enter texts by pressing keys one-by-one.
   * Cons:
   *   it's relatively slow in case of long texts.
   */
  SEND_KEYS,

  /**
   * Set value using JavaScript
   *
   * Pros:
   *   it's faster than "sendKeys" because it types the whole text with just one JavaScript call.
   * Cons:
   *   some people say it's "unnatural" way: it doesn't fill the value exactly the same way as real user do, thus
   *   causing a risk that such method causes false positive or false negative tests.
   */
  JS
}
