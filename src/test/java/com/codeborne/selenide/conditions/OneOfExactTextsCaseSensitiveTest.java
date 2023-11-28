package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static com.codeborne.selenide.Mocks.mockElement;
import static com.codeborne.selenide.TextCheck.FULL_TEXT;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class OneOfExactTextsCaseSensitiveTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(FULL_TEXT));
  private final Collection<String> collectionWithNull = Arrays.asList("John", "Lucy", null, "Banana");
  private final Collection<String> collectionWithEmptyString = List.of("John", "Lucy", "", "Banana");
  private final Collection<String> collectionWithBlankString = List.of("John", "Lucy", "   ", "Banana");
  private final Collection<String> regularCollection = List.of(" John  ", " Lucy", "Vodka", "  Banana    ");
  private final OneOfExactTextsCaseSensitive regularCollectionArgument = new OneOfExactTextsCaseSensitive(regularCollection);

  @Test
  void shouldNotMatchRegularCollectionDifferentCases() {
    assertThat(regularCollectionArgument
      .check(driver, mockElement("bAnAnA")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldMatchRegularCollectionSameCases() {
    assertThat(regularCollectionArgument.check(driver, mockElement("Banana")).verdict()).isEqualTo(ACCEPT);
    assertThat(regularCollectionArgument.check(driver, mockElement("Lucy")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatchRegularCollection() {
    assertThat(regularCollectionArgument.check(driver, mockElement("Banan")).verdict()).isEqualTo(REJECT);
    assertThat(regularCollectionArgument.check(driver, mockElement("Nice Banana")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldThrowExceptionBecauseOfNullCollectionItem() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfExactTextsCaseSensitive(collectionWithNull));
  }

  @Test
  void shouldThrowExceptionForEmptyCollection() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfExactTextsCaseSensitive(emptyList()));
  }

  @Test
  void shouldMatchCollectionWithEmptyStringIfElementsTextIsAlsoEmpty() {
    assertThat(new OneOfExactTextsCaseSensitive(collectionWithEmptyString)
      .check(driver, mockElement("")).verdict()).isEqualTo(ACCEPT);
    assertThat(new OneOfExactTextsCaseSensitive(collectionWithBlankString)
      .check(driver, mockElement("")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatchCollectionWithEmptyStringIfElementsTextIsNotEmptyAndDoesntMatchAnotherStrings() {
    assertThat(new OneOfExactTextsCaseSensitive(collectionWithEmptyString)
      .check(driver, mockElement("apple")).verdict()).isEqualTo(REJECT);
    assertThat(new OneOfExactTextsCaseSensitive(collectionWithBlankString)
      .check(driver, mockElement("apple")).verdict()).isEqualTo(REJECT);
  }

}
