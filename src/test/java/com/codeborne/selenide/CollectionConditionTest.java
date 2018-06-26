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
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.instanceOf;

class CollectionConditionTest {
  @Test
  void testSizeIsEmptyListSize() {
    CollectionCondition collectionCondition = CollectionCondition.size(10);
    MatcherAssert.assertThat(collectionCondition, instanceOf(ListSize.class));
  }

  @Test
  void testSizeGreaterThan() {
    CollectionCondition collectionCondition = CollectionCondition.sizeGreaterThan(10);
    MatcherAssert.assertThat(collectionCondition, instanceOf(SizeGreaterThan.class));
  }

  @Test
  void testSizeGraterThenOrEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeGreaterThanOrEqual(10);
    MatcherAssert.assertThat(collectionCondition, instanceOf(SizeGreaterThanOrEqual.class));
  }

  @Test
  void testSizeLessThan() {
    CollectionCondition collectionCondition = CollectionCondition.sizeLessThan(10);
    MatcherAssert.assertThat(collectionCondition, instanceOf(SizeLessThan.class));
  }

  @Test
  void testSizeLessThanOrEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeLessThanOrEqual(10);
    MatcherAssert.assertThat(collectionCondition, instanceOf(SizeLessThanOrEqual.class));
  }

  @Test
  void testSizeNotEqual() {
    CollectionCondition collectionCondition = CollectionCondition.sizeNotEqual(10);
    MatcherAssert.assertThat(collectionCondition, instanceOf(SizeNotEqual.class));
  }

  @Test
  void testTextsWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.texts("One", "Two", "Three");
    MatcherAssert.assertThat(collectionCondition, instanceOf(Texts.class));
    Assertions.assertEquals("Texts [One, Two, Three]", collectionCondition.toString(), "Texts content");
  }

  @Test
  void testTextsWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.texts(asList("One", "Two", "Three"));
    MatcherAssert.assertThat(collectionCondition, instanceOf(Texts.class));
    Assertions.assertEquals("Texts [One, Two, Three]", collectionCondition.toString(), "Texts content");
  }

  @Test
  void testExactTextsWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.exactTexts("One", "Two", "Three");
    MatcherAssert.assertThat(collectionCondition, instanceOf(ExactTexts.class));
    Assertions.assertEquals("Exact texts [One, Two, Three]", collectionCondition.toString(), "Exact texts content");
  }

  @Test
  void testExactTextsWithListOfStrings() {
    CollectionCondition collectionCondition = CollectionCondition.exactTexts(asList("One", "Two", "Three"));
    MatcherAssert.assertThat(collectionCondition, instanceOf(ExactTexts.class));
    Assertions.assertEquals("Exact texts [One, Two, Three]", collectionCondition.toString(), "Exact texts content");
  }

  @Test
  void testTextsInAnyOrderWithObjectsList() {
    CollectionCondition collectionCondition = CollectionCondition.textsInAnyOrder("One", "Two", "Three");
    MatcherAssert.assertThat(collectionCondition, instanceOf(TextsInAnyOrder.class));
    Assertions.assertEquals("TextsInAnyOrder [One, Two, Three]", collectionCondition.toString(), "Text in any order content");
  }

  @Test
  void testTextsInAnyOrderWithStringsList() {
    CollectionCondition collectionCondition = CollectionCondition.textsInAnyOrder(asList("One", "Two", "Three"));
    MatcherAssert.assertThat(collectionCondition, instanceOf(TextsInAnyOrder.class));
    Assertions.assertEquals("TextsInAnyOrder [One, Two, Three]", collectionCondition.toString(), "Text in any order content");
  }
}
