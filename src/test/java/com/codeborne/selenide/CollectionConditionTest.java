package com.codeborne.selenide;

import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.ExactTextsCaseSensitive;
import com.codeborne.selenide.collections.Texts;
import com.codeborne.selenide.collections.TextsInAnyOrder;
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
  void exactTextsCaseSensitiveWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.exactTextsCaseSensitive("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(ExactTextsCaseSensitive.class);
    assertThat(collectionCondition)
      .as("Exact texts case sensitive content")
      .hasToString("Exact texts case sensitive [One, Two, Three]");
  }

  @Test
  void exactTextsCaseSensitiveWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.exactTextsCaseSensitive(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(ExactTextsCaseSensitive.class);
    assertThat(collectionCondition)
      .as("Exact texts case sensitive content")
      .hasToString("Exact texts case sensitive [One, Two, Three]");
  }

  @Test
  void textsInAnyOrderWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.textsInAnyOrder("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(TextsInAnyOrder.class);
    assertThat(collectionCondition)
      .as("Text in any order content")
      .hasToString("TextsInAnyOrder [One, Two, Three]");
  }

  @Test
  void textsInAnyOrderWithStringsList() {
    CollectionCondition collectionCondition = CollectionCondition.textsInAnyOrder(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(TextsInAnyOrder.class);
    assertThat(collectionCondition)
      .as("Text in any order content")
      .hasToString("TextsInAnyOrder [One, Two, Three]");
  }

  @Test
  void explanationIsIncludedToString() {
    CollectionCondition collectionCondition = CollectionCondition.texts("One").because("should be");
    assertThat(collectionCondition)
      .as("Should contain explanation")
      .hasToString("texts [One] (because should be)");
  }
}
