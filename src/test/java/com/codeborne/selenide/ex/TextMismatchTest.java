package com.codeborne.selenide.ex;

import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

final class TextMismatchTest implements WithAssertions {
  private final WebElementsCollection collection = mockCollection("Collection description");
  private final List<String> actualTexts = asList("One", "Two", "Three");
  private final List<String> expectedTexts = asList("Four", "Five", "Six");
  private final long timeoutMs = 1000L;

  @BeforeEach
  void setUp() {
    when(collection.driver()).thenReturn(new DriverStub());
  }

  @Test
  void toString_withoutExplanation() {
    TextsMismatch textsMismatch = new TextsMismatch(collection, actualTexts, expectedTexts, null, timeoutMs);

    assertThat(textsMismatch).hasMessage(String.format("Texts mismatch%n" +
      "Actual: [One, Two, Three]%n" +
      "Expected: [Four, Five, Six]%n" +
      "Collection: Collection description%n" +
      "Screenshot: null%n" +
      "Timeout: 1 s."));
  }

  @Test
  void toString_withExplanation() {
    TextsMismatch textsMismatch = new TextsMismatch(collection, actualTexts, expectedTexts, "it's said in doc", timeoutMs);

    assertThat(textsMismatch).hasMessage(String.format("Texts mismatch%n" +
      "Actual: [One, Two, Three]%n" +
      "Expected: [Four, Five, Six]%n" +
      "Because: it's said in doc%n" +
      "Collection: Collection description%n" +
      "Screenshot: null%n" +
      "Timeout: 1 s."));
  }
}
