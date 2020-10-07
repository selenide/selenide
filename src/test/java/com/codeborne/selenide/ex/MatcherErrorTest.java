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
    assertThat(new MatcherError("foo", "blah", null, mockCollection(".rows"), actualElements, ex, 4000).getMessage()).isEqualTo(
      "Collection matcher error" + lineSeparator() +
        "Expected: foo of elements to match [blah] predicate" + lineSeparator() +
        "Collection: .rows" + lineSeparator() +
        "Elements: [" + lineSeparator() +
        "\t<div displayed:false>mr. %First</div>," + lineSeparator() +
        "\t<div displayed:false>mr. %Second</div>" + lineSeparator() +
        "]" + lineSeparator() +
        "Screenshot: null" + lineSeparator() +
        "Timeout: 4 s." + lineSeparator() +
        "Caused by: NoSuchElementException: .third");
  }

  @Test
  void message_whtExplanation() {
    assertThat(new MatcherError("foo", "blah", "I think so", mockCollection(".rows"), actualElements, ex, 4000).getMessage()).isEqualTo(
      "Collection matcher error" + lineSeparator() +
        "Expected: foo of elements to match [blah] predicate" + lineSeparator() +
        "Because: I think so" + lineSeparator() +
        "Collection: .rows" + lineSeparator() +
        "Elements: [" + lineSeparator() +
        "\t<div displayed:false>mr. %First</div>," + lineSeparator() +
        "\t<div displayed:false>mr. %Second</div>" + lineSeparator() +
        "]" + lineSeparator() +
        "Screenshot: null" + lineSeparator() +
        "Timeout: 4 s." + lineSeparator() +
        "Caused by: NoSuchElementException: .third");
  }
}
