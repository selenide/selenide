package com.codeborne.selenide.ex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SoftAssertionErrorTest {

  @Test
  public void testConstructor() {
    SoftAssertionError softAssertionTest = new SoftAssertionError("Error message");
    String expectedString = "com.codeborne.selenide.ex.SoftAssertionError: Error message";
    assertEquals(expectedString, softAssertionTest.toString());
  }
}
