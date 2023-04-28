package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class MatcherErrorTest {
  private final List<WebElement> actualElements = asList(mockElement("mr. %First"), mockElement("mr. %Second"));
  private final WebDriverException ex = new NoSuchElementException(".third");

  @Test
  void message() {
    MatcherError error = new MatcherError(null,
      "all of elements to match [class=active] predicate",
      "<actual-elements>",
      mockCollection(".rows"), ex, 4000);
    assertThat(error.getMessage()).isEqualTo(
      "Collection matcher error" + lineSeparator() +
        "Expected: all of elements to match [class=active] predicate" + lineSeparator() +
        "Collection: .rows" + lineSeparator() +
        "Elements: <actual-elements>" + lineSeparator() +
        "Timeout: 4 s." + lineSeparator() +
        "Caused by: NoSuchElementException: .third");
  }

  @Test
  void message_withExplanation() {
    assertThat(new MatcherError("I think so",
      "some of elements to match [class=active] predicate",
      "<actual-elements>", mockCollection(".rows"), ex, 4000).getMessage()).isEqualTo(
      "Collection matcher error" + lineSeparator() +
        "Expected: some of elements to match [class=active] predicate" + lineSeparator() +
        "Because: I think so" + lineSeparator() +
        "Collection: .rows" + lineSeparator() +
        "Elements: <actual-elements>" + lineSeparator() +
        "Timeout: 4 s." + lineSeparator() +
        "Caused by: NoSuchElementException: .third");
  }
}
