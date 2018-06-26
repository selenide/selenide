package com.codeborne.selenide.ex;

import java.util.List;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;

class TextMismatchTest {

  @Test
  void testToString() {
    WebElementsCollection webElementsCollection = mock(WebElementsCollection.class);
    List<String> actualTexts = asList("One", "Two", "Three");
    List<String> expectedTexts = asList("Four", "Five", "Six");
    long timeoutMs = 1000L;
    TextsMismatch textsMismatch = new TextsMismatch(webElementsCollection, actualTexts, expectedTexts, timeoutMs);
    String expectedString = "TextsMismatch \n" +
      "Actual: [One, Two, Three]\n" +
      "Expected: [Four, Five, Six]\n" +
      "Collection: null\n" +
      "Screenshot: null\n" +
      "Timeout: 1 s.";
    Assertions.assertEquals(expectedString, textsMismatch.toString());
  }
}
