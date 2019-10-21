package com.codeborne.selenide.ex;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static java.util.Arrays.asList;

class TextsMismatchTest implements WithAssertions {
  @Test
  void errorMessage() {
    List<String> actualTexts = asList("Niff", "Naff", "Nuff");
    List<String> expectedTexts = asList("Piff", "Paff", "Puff");
    TextsMismatch textsMismatch = new TextsMismatch(mockCollection(".characters"),
      actualTexts,
      expectedTexts,
      "we expect favorite characters", 9000);

    assertThat(textsMismatch).hasMessage("Texts mismatch\n" +
      "Actual: [Niff, Naff, Nuff]\n" +
      "Expected: [Piff, Paff, Puff]\n" +
      "Because: we expect favorite characters\n" +
      "Collection: .characters\n" +
      "Screenshot: null\n" +
      "Timeout: 9 s.");
  }
}
