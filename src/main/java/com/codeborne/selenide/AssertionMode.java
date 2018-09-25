package com.codeborne.selenide;

/**
 * Assertion modes available
 */
public enum AssertionMode {
  /**
   * Default mode - tests are failing immediately
   */
  STRICT,
  /**
   * Test are failing only at the end of the methods.
   */
  SOFT
}
