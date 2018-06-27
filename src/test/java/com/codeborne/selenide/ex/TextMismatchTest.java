package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TextMismatchTest {
  WebElementsCollection webElementsCollection = mock(WebElementsCollection.class);
  List<String> actualTexts = asList("One", "Two", "Three");
  List<String> expectedTexts = asList("Four", "Five", "Six");
  long timeoutMs = 1000L;

  @Test
  public void toString_withoutExplanation() {
    TextsMismatch textsMismatch = new TextsMismatch(webElementsCollection, actualTexts, expectedTexts, null, timeoutMs);
    String expectedString = "TextsMismatch \n" +
      "Actual: [One, Two, Three]\n" +
      "Expected: [Four, Five, Six]\n" +
      "Collection: null\n" +
      "Screenshot: null\n" +
      "Timeout: 1 s.";
    assertEquals(expectedString, textsMismatch.toString());
  }

  @Test
  public void toString_withExplanation() {
    TextsMismatch textsMismatch = new TextsMismatch(webElementsCollection, actualTexts, expectedTexts, "it's said in doc", timeoutMs);
    String expectedString = "TextsMismatch \n" +
      "Actual: [One, Two, Three]\n" +
      "Expected: [Four, Five, Six]\n" +
      "Because: it's said in doc\n" +
      "Collection: null\n" +
      "Screenshot: null\n" +
      "Timeout: 1 s.";
    assertEquals(expectedString, textsMismatch.toString());
  }
}
