package com.codeborne.selenide.collections;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ExactTextsTest {
  @Test
  public void testName() {
    try {
      new ExactTexts();
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      assertThat(expected.getMessage(), is("Array of expected texts is empty"));
    }
  }
}
