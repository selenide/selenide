package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.TextCheck;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class OneOfExactTextsTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(TextCheck.FULL_TEXT));
  private final Collection<String> collectionWithNull = new HashSet<>(Arrays.asList("John", "Lucy", null, "Banana"));
  private final Collection<String> collectionWithEmptyString = List.of("John", "Lucy", "", "Banana");
  private final Collection<String> collectionWithBlankString = List.of("John", "Lucy", "   ", "Banana");
  private final Collection<String> regularCollection = List.of("John", "Lucy", "Vodka", "  Banana   ");

  @Test
  void shouldMatchRegularCollectionDifferentCases() {
    assertThat(new OneOfExactTexts(regularCollection).check(driver, mockElement("bAnAnA")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatchRegularCollection() {
    assertThat(new OneOfExactTexts(regularCollection).check(driver, mockElement("Banan")).verdict()).isEqualTo(REJECT);
    assertThat(new OneOfExactTexts(regularCollection).check(driver, mockElement("Bananaa")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldThrowExceptionBecauseOfNullCollectionItem() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfExactTexts(collectionWithNull));
  }

  @Test
  void shouldThrowExceptionForEmptyCollection() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfExactTexts(emptyList()));
  }

  @Test
  void shouldMatchCollectionWithEmptyStringIfElementsTextIsAlsoEmpty() {
    assertThat(new OneOfExactTexts(collectionWithEmptyString).check(driver, mockElement("")).verdict()).isEqualTo(ACCEPT);
    assertThat(new OneOfExactTexts(collectionWithBlankString).check(driver, mockElement("")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatchCollectionWithEmptyStringIfElementsTextIsNotEmptyAndDoesntMatchAnotherStrings() {
    assertThat(new OneOfExactTexts(collectionWithEmptyString).check(driver, mockElement("apple")).verdict()).isEqualTo(REJECT);
    assertThat(new OneOfExactTexts(collectionWithBlankString).check(driver, mockElement("apple")).verdict()).isEqualTo(REJECT);
  }
}
