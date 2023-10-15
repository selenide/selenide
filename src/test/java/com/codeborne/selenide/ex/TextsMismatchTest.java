package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.CollectionSource;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class TextsMismatchTest {
  private final CollectionSource collection = mockCollection(".characters");
  private final List<String> actualTexts = asList("Niff", "Naff", "Nuff");
  private final List<String> expectedTexts = asList("Piff", "Paff", "Puff");

  @Test
  void errorMessage_withoutExplanation() {
    TextsMismatch error = new TextsMismatch("Texts mismatch", collection, expectedTexts, actualTexts, null, 9000, null);

    assertThat(error).hasMessage(String.format("Texts mismatch%n" +
      "Actual (3): [Niff, Naff, Nuff]%n" +
      "Expected (3): [Piff, Paff, Puff]%n" +
      "Collection: .characters%n" +
      "Timeout: 9 s."));
  }

  @Test
  void errorMessage_withExplanation() {
    String explanation = "we expect favorite characters";
    TextsMismatch error = new TextsMismatch("Texts mismatch", collection, expectedTexts, actualTexts, explanation, 9000, null);

    assertThat(error).hasMessage(String.format("Texts mismatch%n" +
      "Actual (3): [Niff, Naff, Nuff]%n" +
      "Expected (3): [Piff, Paff, Puff]%n" +
      "Because: we expect favorite characters%n" +
      "Collection: .characters%n" +
      "Timeout: 9 s."));
  }

  @Test
  void errorMessage_withDifferentSize() {
    TextsMismatch error = new TextsMismatch("Texts mismatch", collection, asList("Chip", "Dale"), actualTexts, null, 9000, null);

    assertThat(error).hasMessage(String.format("Texts mismatch%n" +
                                               "Actual (3): [Niff, Naff, Nuff]%n" +
                                               "Expected (2): [Chip, Dale]%n" +
                                               "Collection: .characters%n" +
                                               "Timeout: 9 s."));
  }

}
