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
    ListSizeMismatch textsMismatch = new ListSizeMismatch("=", expectedTexts.size(), actualTexts.size(),
      null, mockCollection(".characters"), null, 9000);

    assertThat(textsMismatch).hasMessageContaining(
      "List size mismatch",
      "expected: = 3, actual: 3",
      "Collection: .characters",
      "Timeout: 9 s."
    );
  }

  @Test
  void errorMessage_withExplanation() {
    ListSizeMismatch textsMismatch = new ListSizeMismatch("=", expectedTexts.size(), actualTexts.size(),
      "we expect favorite characters", mockCollection(".characters"), null, 9000);

    assertThat(textsMismatch).hasMessageContaining(
      "List size mismatch",
      "expected: = 3, actual: 3",
      "Because: we expect favorite characters",
      "Collection: .characters",
      "Timeout: 9 s."
    );
  }
}
