package com.codeborne.selenide;

import com.codeborne.selenide.collections.ContainExactTextsCaseSensitive;
import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.collections.ExactTextsCaseSensitive;
import com.codeborne.selenide.collections.ExactTextsCaseSensitiveInAnyOrder;
import com.codeborne.selenide.collections.ListSize;
import com.codeborne.selenide.collections.SizeGreaterThan;
import com.codeborne.selenide.collections.SizeGreaterThanOrEqual;
import com.codeborne.selenide.collections.SizeLessThan;
import com.codeborne.selenide.collections.SizeLessThanOrEqual;
import com.codeborne.selenide.collections.SizeNotEqual;
import com.codeborne.selenide.collections.Texts;
import com.codeborne.selenide.collections.TextsInAnyOrder;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class CollectionConditionTest {
  @Test
  void exactSize() {
    assertThat(CollectionCondition.size(10)).isInstanceOf(ListSize.class);
  }

  @Test
  void sizeGreaterThan() {
    assertThat(CollectionCondition.sizeGreaterThan(10)).isInstanceOf(SizeGreaterThan.class);
  }

  @Test
  void sizeGraterThenOrEqual() {
    assertThat(CollectionCondition.sizeGreaterThanOrEqual(10))
      .isInstanceOf(SizeGreaterThanOrEqual.class);
  }

  @Test
  void sizeLessThan() {
    assertThat(CollectionCondition.sizeLessThan(10))
      .isInstanceOf(SizeLessThan.class);
  }

  @Test
  void sizeLessThanOrEqual() {
    assertThat(CollectionCondition.sizeLessThanOrEqual(10))
      .isInstanceOf(SizeLessThanOrEqual.class);
  }

  @Test
  void sizeNotEqual() {
    assertThat(CollectionCondition.sizeNotEqual(10))
      .isInstanceOf(SizeNotEqual.class);
  }

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

  @Test
  void exactTextsCaseSensitiveInAnyOrderWithList() {
    CollectionCondition condition = CollectionCondition.exactTextsCaseSensitiveInAnyOrder(asList("One", "Two"));
    assertThat(condition).isInstanceOf(ExactTextsCaseSensitiveInAnyOrder.class);
  }

  @Test
  void exactTextsCaseSensitiveInAnyOrderWithVarargs() {
    CollectionCondition condition = CollectionCondition.exactTextsCaseSensitiveInAnyOrder("One", "Two");
    assertThat(condition).isInstanceOf(ExactTextsCaseSensitiveInAnyOrder.class);
  }

  @Test
  void containTextsWithStringList() {
    CollectionCondition condition = CollectionCondition.containExactTextsCaseSensitive(asList("One", "Two", "Three"));
    assertThat(condition).isInstanceOf(ContainExactTextsCaseSensitive.class);
  }

  @Test
  void containTextsWithVarargs() {
    CollectionCondition condition = CollectionCondition.containExactTextsCaseSensitive("One", "Two", "Three");
    assertThat(condition).isInstanceOf(ContainExactTextsCaseSensitive.class);
  }
}
