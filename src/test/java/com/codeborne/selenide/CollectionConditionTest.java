package com.codeborne.selenide;

import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.Texts;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class CollectionConditionTest {
  @Test
  void textsWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.texts("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(Texts.class);
    assertThat(collectionCondition)
      .as("Texts content")
      .hasToString("texts [One, Two, Three]");
  }

  @Test
  void textsWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.texts(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(Texts.class);
    assertThat(collectionCondition)
      .as("Texts content")
      .hasToString("texts [One, Two, Three]");
  }

  @Test
  void exactTextsWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.exactTexts("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(ExactTexts.class);
    assertThat(collectionCondition)
      .as("Exact texts content")
      .hasToString("Exact texts [One, Two, Three]");
  }

  @Test
  void exactTextsWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.exactTexts(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(ExactTexts.class);
    assertThat(collectionCondition)
      .as("Exact texts content")
      .hasToString("Exact texts [One, Two, Three]");
  }

  @Test
  void explanationIsIncludedToString() {
    CollectionCondition collectionCondition = CollectionCondition.texts("One").because("should be");
    assertThat(collectionCondition)
      .as("Should contain explanation")
      .hasToString("texts [One] (because should be)");
  }
}
