package com.codeborne.selenide;

import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.Texts;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.texts;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class CollectionConditionTest {
  @Test
  void textsWithObjectsList() {
    WebElementsCondition collectionCondition = texts("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(Texts.class);
    assertThat(collectionCondition)
      .as("Texts content")
      .hasToString("texts [One, Two, Three]");
  }

  @Test
  void textsWithListOfStrings() {
    WebElementsCondition collectionCondition = texts(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(Texts.class);
    assertThat(collectionCondition)
      .as("Texts content")
      .hasToString("texts [One, Two, Three]");
  }

  @Test
  void exactTextsWithObjectsList() {
    WebElementsCondition collectionCondition = exactTexts("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(ExactTexts.class);
    assertThat(collectionCondition)
      .as("Exact texts content")
      .hasToString("Exact texts [One, Two, Three]");
  }

  @Test
  void exactTextsWithListOfStrings() {
    WebElementsCondition collectionCondition = exactTexts(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(ExactTexts.class);
    assertThat(collectionCondition)
      .as("Exact texts content")
      .hasToString("Exact texts [One, Two, Three]");
  }
}
