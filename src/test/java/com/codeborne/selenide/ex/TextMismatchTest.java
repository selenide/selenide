package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TextMismatchTest {

  @Test
  public void testToString() {
    WebElementsCollection webElementsCollection = Mockito.mock(WebElementsCollection.class);
    List<String> actualTexts = Arrays.asList("One", "Two", "Three");
    List<String> expectedTexts = Arrays.asList("Four", "Five", "Six");
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
