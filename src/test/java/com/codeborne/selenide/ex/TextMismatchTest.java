package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TextMismatchTest {

  @Test
  public void testToString() {
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
    assertEquals(expectedString, textsMismatch.toString());
  }

}
