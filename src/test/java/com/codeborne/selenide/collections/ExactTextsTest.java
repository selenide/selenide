package com.codeborne.selenide.collections;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ExactTextsTest {
  @Test
  public void emptyArrayIsNotAllowed() {
    try {
      new ExactTexts();
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      assertThat(expected.getMessage(), is("No expected texts given"));
    }
  }

  @Test
  public void emptyListIsNotAllowed() {
    try {
      new ExactTexts(new ArrayList<>());
      fail("expected IllegalArgumentException");
    }
    catch (IllegalArgumentException expected) {
      assertThat(expected.getMessage(), is("No expected texts given"));
    }
  }
}
