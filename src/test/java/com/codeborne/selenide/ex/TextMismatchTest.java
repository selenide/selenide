package com.codeborne.selenide.ex;

import java.util.List;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TextMismatchTest implements WithAssertions {
  private WebElementsCollection webElementsCollection = mock(WebElementsCollection.class);
  private List<String> actualTexts = asList("One", "Two", "Three");
  private List<String> expectedTexts = asList("Four", "Five", "Six");
  private long timeoutMs = 1000L;

  @BeforeEach
  void setUp() {
    when(webElementsCollection.driver()).thenReturn(new DriverStub());
  }

  @Test
  void toString_withoutExplanation() {
    TextsMismatch textsMismatch = new TextsMismatch(webElementsCollection, actualTexts, expectedTexts, null, timeoutMs);
    String expectedString = "TextsMismatch \n" +
      "Actual: [One, Two, Three]\n" +
      "Expected: [Four, Five, Six]\n" +
      "Collection: null\n" +
      "Screenshot: null\n" +
      "Timeout: 1 s.";
    assertThat(textsMismatch)
      .hasToString(expectedString);
  }

  @Test
  void toString_withExplanation() {
    TextsMismatch textsMismatch = new TextsMismatch(webElementsCollection, actualTexts, expectedTexts, "it's said in doc", timeoutMs);
    String expectedString = "TextsMismatch \n" +
      "Actual: [One, Two, Three]\n" +
      "Expected: [Four, Five, Six]\n" +
      "Because: it's said in doc\n" +
      "Collection: null\n" +
      "Screenshot: null\n" +
      "Timeout: 1 s.";
    assertThat(textsMismatch)
      .hasToString(expectedString);
  }
}
