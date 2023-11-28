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

class OneOfTextsTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(TextCheck.FULL_TEXT));
  private final Collection<String> collectionWithNull = new HashSet<>(Arrays.asList("John", "Lucy", null, "Banana"));
  private final Collection<String> collectionWithEmptyString = List.of("John", "Lucy", "", "Banana");
  private final Collection<String> collectionWithBlankString = List.of("John", "Lucy", "   ", "Banana");
  private final Collection<String> regularCollection = List.of("John", "Lucy", "Vod  ka", "  Banana   fruit  ");

  @Test
  void shouldMatchRegularCollectionDifferentCases() {
    assertThat(new OneOfTexts(regularCollection).check(driver,
      mockElement("My favorite fruit is bbAnAnA fRuItt of course")).verdict()).isEqualTo(ACCEPT);
    assertThat(new OneOfTexts(regularCollection).check(driver,
      mockElement("My favorite is of course bbbAnAnA frUit")).verdict()).isEqualTo(ACCEPT);
    assertThat(new OneOfTexts(regularCollection).check(driver,
      mockElement("bAnAnA fRuiT My favorite is of course")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatchRegularCollection() {
    assertThat(new OneOfTexts(regularCollection).check(driver,
      mockElement("My favorite fruit is Banan fruit of course")).verdict()).isEqualTo(REJECT);
    assertThat(new OneOfTexts(regularCollection).check(driver,
      mockElement("Banana ruit My favorite fruit is  of course")).verdict()).isEqualTo(REJECT);
    assertThat(new OneOfTexts(regularCollection).check(driver,
      mockElement("My favorite fruit is of course Banana fruiit")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldThrowExceptionBecauseOfNullCollectionItem() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfTexts(collectionWithNull));
  }

  @Test
  void shouldThrowExceptionForEmptyCollection() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfTexts(emptyList()));
  }

  @Test
  void shouldThrowExceptionIfCollectionContainsBlankStrings() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfTexts(collectionWithEmptyString));
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfTexts(collectionWithBlankString));
  }
}
