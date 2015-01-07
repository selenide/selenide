package com.codeborne.selenide.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class SelenideLoggerTest {

  @Test
  public void convertsJavaMethodNameToHumanReadableClause() {
    assertEquals("click", SelenideLogger.readableMethodName("click"));
    assertEquals("set value", SelenideLogger.readableMethodName("setValue"));
    assertEquals("should be", SelenideLogger.readableMethodName("shouldBe"));
    assertEquals("converts java method name to human readable clause", 
        SelenideLogger.readableMethodName("convertsJavaMethodNameToHumanReadableClause"));
  }

  @Test
  public void printsReadableArgumentsValues() {
    assertEquals("", SelenideLogger.readableArguments(null));
    assertEquals("111", SelenideLogger.readableArguments(111));
    assertEquals("[1, 2, 3]", SelenideLogger.readableArguments(1, 2, 3));
    assertEquals("a", SelenideLogger.readableArguments(new String[] {"a"}));
    assertEquals("[a, bb]", SelenideLogger.readableArguments(new String[] {"a", "bb"}));
  }
}