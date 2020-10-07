package com.codeborne.selenide;

import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.ListSize;
import com.codeborne.selenide.collections.SizeGreaterThan;
import com.codeborne.selenide.collections.SizeGreaterThanOrEqual;
import com.codeborne.selenide.collections.SizeLessThan;
import com.codeborne.selenide.collections.SizeLessThanOrEqual;
import com.codeborne.selenide.collections.SizeNotEqual;
import com.codeborne.selenide.collections.Texts;
import com.codeborne.selenide.collections.TextsInAnyOrder;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

final class CollectionConditionTest implements WithAssertions {
  @Test
  void testSizeIsEmptyListSize() {
    CollectionCondition collectionCondition = CollectionCondition.size(10);
    assertThat(collectionCondition)
      .isInstanceOf(ListSize.class);
  }

  @Test
  void testSizeGreaterThan() {
    CollectionCondition collectionCondition = CollectionCondition.sizeGreaterThan(10);
    assertThat(collectionCondition)
      .isInstanceOf(SizeGreaterThan.class);
  }

  @Test
  void testSizeGraterThenOrEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeGreaterThanOrEqual(10);
    assertThat(collectionCondition)
      .isInstanceOf(SizeGreaterThanOrEqual.class);
  }

  @Test
  void testSizeLessThan() {
    CollectionCondition collectionCondition = CollectionCondition.sizeLessThan(10);
    assertThat(collectionCondition)
      .isInstanceOf(SizeLessThan.class);
  }

  @Test
  void testSizeLessThanOrEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeLessThanOrEqual(10);
    assertThat(collectionCondition)
      .isInstanceOf(SizeLessThanOrEqual.class);
  }

  @Test
  void testSizeNotEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeNotEqual(10);
    assertThat(collectionCondition)
      .isInstanceOf(SizeNotEqual.class);
  }

  @Test
  void testTextsWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.texts("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(Texts.class);
    assertThat(collectionCondition)
      .as("Texts content")
      .hasToString("texts [One, Two, Three]");
  }

  @Test
  void testTextsWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.texts(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(Texts.class);
    assertThat(collectionCondition)
      .as("Texts content")
      .hasToString("texts [One, Two, Three]");
  }

  @Test
  void testExactTextsWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.exactTexts("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(ExactTexts.class);
    assertThat(collectionCondition)
      .as("Exact texts content")
      .hasToString("Exact texts [One, Two, Three]");
  }

  @Test
  void testExactTextsWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.exactTexts(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(ExactTexts.class);
    assertThat(collectionCondition)
      .as("Exact texts content")
      .hasToString("Exact texts [One, Two, Three]");
  }

  @Test
  void testTextsInAnyOrderWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.textsInAnyOrder("One", "Two", "Three");
    assertThat(collectionCondition)
      .isInstanceOf(TextsInAnyOrder.class);
    assertThat(collectionCondition)
      .as("Text in any order content")
      .hasToString("TextsInAnyOrder [One, Two, Three]");
  }

  @Test
  void testTextsInAnyOrderWithStringsList() {
    CollectionCondition collectionCondition = CollectionCondition.textsInAnyOrder(asList("One", "Two", "Three"));
    assertThat(collectionCondition)
      .isInstanceOf(TextsInAnyOrder.class);
    assertThat(collectionCondition)
      .as("Text in any order content")
      .hasToString("TextsInAnyOrder [One, Two, Three]");
  }

  @Test
  void testExplanationIsIncludedToString() {
    CollectionCondition collectionCondition = CollectionCondition.texts("One").because("should be");
    assertThat(collectionCondition)
      .as("Should contain explanation")
      .hasToString("texts [One] (because should be)");
  }
}
