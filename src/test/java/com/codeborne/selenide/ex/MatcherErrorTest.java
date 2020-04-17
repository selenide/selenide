package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class MatcherErrorTest {
  private final List<WebElement> actualElements = asList(mockElement("mr. %First"), mockElement("mr. %Second"));
  private final WebDriverException ex = new NoSuchElementException(".third");

  @Test
  void message() {
    assertThat(new MatcherError("foo", "blah", null, mockCollection(".rows"), actualElements, ex, 4000).getMessage()).isEqualTo(
      "Collection matcher error\n" +
        "Expected: foo of elements to match [blah] predicate\n" +
        "Collection: .rows\n" +
        "Elements: [\n" +
        "\t<div displayed:false>mr. %First</div>,\n" +
        "\t<div displayed:false>mr. %Second</div>\n" +
        "]\n" +
        "Screenshot: null\n" +
        "Timeout: 4 s.\n" +
        "Caused by: NoSuchElementException: .third");
  }

  @Test
  void message_whtExplanation() {
    assertThat(new MatcherError("foo", "blah", "I think so", mockCollection(".rows"), actualElements, ex, 4000).getMessage()).isEqualTo(
      "Collection matcher error\n" +
        "Expected: foo of elements to match [blah] predicate\n" +
        "Because: I think so\n" +
        "Collection: .rows\n" +
        "Elements: [\n" +
        "\t<div displayed:false>mr. %First</div>,\n" +
        "\t<div displayed:false>mr. %Second</div>\n" +
        "]\n" +
        "Screenshot: null\n" +
        "Timeout: 4 s.\n" +
        "Caused by: NoSuchElementException: .third");
  }
}
