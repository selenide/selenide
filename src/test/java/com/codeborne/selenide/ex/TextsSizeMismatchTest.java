package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class TextsSizeMismatchTest {
  private final List<String> actualTexts = asList("Niff", "Naff", "Nuff%");
  private final List<String> expectedTexts = asList("Piff", "Paff", "Puff'\"bro");

  @Test
  void errorMessage() {
    TextsSizeMismatch textsMismatch = new TextsSizeMismatch(mockCollection(".characters"),
        expectedTexts, actualTexts,
        null, 9000);

    assertThat(textsMismatch).hasMessage(String.format("Texts size mismatch%n" +
      "Actual: [Niff, Naff, Nuff%%], List size: 3%n" +
      "Expected: [Piff, Paff, Puff'\"bro], List size: 3%n" +
      "Collection: .characters%n" +
      "Timeout: 9 s."));
  }

  @Test
  void errorMessage_withExplanation() {
    TextsSizeMismatch textsMismatch = new TextsSizeMismatch(mockCollection(".characters"),
        expectedTexts, actualTexts,
        "we expect favorite characters", 9000);

    assertThat(textsMismatch).hasMessage(String.format("Texts size mismatch%n" +
      "Actual: [Niff, Naff, Nuff%%], List size: 3%n" +
      "Expected: [Piff, Paff, Puff'\"bro], List size: 3%n" +
      "Because: we expect favorite characters%n" +
      "Collection: .characters%n" +
      "Timeout: 9 s."));
  }
}
