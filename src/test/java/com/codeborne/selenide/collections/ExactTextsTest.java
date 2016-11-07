package com.codeborne.selenide.collections;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ExactTextsTest {
  @Test
  public void testEmptyArrayArgument() {
    try {
      new ExactTexts();
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      assertThat(expected.getMessage(), is("Array of expected texts is empty"));
    }
  }

  @Test
  public void testEmptyListArgument() {
    try {
      new ExactTexts(new ArrayList<>());
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      assertThat(expected.getMessage(), is("The list of expected texts is empty"));
    }
  }
}
