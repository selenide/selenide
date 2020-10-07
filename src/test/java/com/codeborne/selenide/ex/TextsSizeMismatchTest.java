package com.codeborne.selenide.ex;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static java.util.Arrays.asList;

final class TextsSizeMismatchTest implements WithAssertions {
  private final List<String> actualTexts = asList("Niff", "Naff", "Nuff%");
  private final List<String> expectedTexts = asList("Piff", "Paff", "Puff'\"bro");

  @Test
  void errorMessage() {
    TextsSizeMismatch textsMismatch = new TextsSizeMismatch(mockCollection(".characters"),
      actualTexts,
      expectedTexts,
      null, 9000);

    assertThat(textsMismatch).hasMessage(String.format("Texts size mismatch%n" +
      "Actual: [Niff, Naff, Nuff%%], List size: 3%n" +
      "Expected: [Piff, Paff, Puff'\"bro], List size: 3%n" +
      "Collection: .characters%n" +
      "Screenshot: null%n" +
      "Timeout: 9 s."));
  }

  @Test
  void errorMessage_withExplanation() {
    TextsSizeMismatch textsMismatch = new TextsSizeMismatch(mockCollection(".characters"),
      actualTexts,
      expectedTexts,
      "we expect favorite characters", 9000);

    assertThat(textsMismatch).hasMessage(String.format("Texts size mismatch%n" +
      "Actual: [Niff, Naff, Nuff%%], List size: 3%n" +
      "Expected: [Piff, Paff, Puff'\"bro], List size: 3%n" +
      "Because: we expect favorite characters%n" +
      "Collection: .characters%n" +
      "Screenshot: null%n" +
      "Timeout: 9 s."));
  }
}
