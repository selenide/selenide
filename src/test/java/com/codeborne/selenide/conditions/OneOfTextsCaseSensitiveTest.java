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

class OneOfTextsCaseSensitiveTest {
  private final Driver driver = new DriverStub(new SelenideConfig().textCheck(TextCheck.FULL_TEXT));

  private final Collection<String> collectionWithNull = new HashSet<>(Arrays.asList("John", "Lucy", null, "Banana"));
  private final Collection<String> collectionWithEmptyString = List.of("John", "Lucy", "", "Banana");
  private final Collection<String> collectionWithBlankString = List.of("John", "Lucy", "   ", "Banana");
  private final Collection<String> regularCollection = List.of("John", "Lucy", "Vod  ka", " Banana   fruit  ");
  private final OneOfTextsCaseSensitive regularCollectionArgument = new OneOfTextsCaseSensitive(regularCollection);

  @Test
  void shouldMatchRegularCollection() {
    assertThat(regularCollectionArgument.check(driver,
      mockElement("My favorite fruit is BBanana fruitT of course")).verdict()).isEqualTo(ACCEPT);
    assertThat(regularCollectionArgument.check(driver,
      mockElement("My favorite fruit is of course bbBanana fruit")).verdict()).isEqualTo(ACCEPT);
    assertThat(regularCollectionArgument.check(driver,
      mockElement("Banana fruitTt My favorite fruit is of course")).verdict()).isEqualTo(ACCEPT);
  }

  @Test
  void shouldNotMatchRegularCollectionDifferentCases() {
    assertThat(regularCollectionArgument.check(driver,
      mockElement("My favorite fruit is bAnAnAaaaaaa of course")).verdict()).isEqualTo(REJECT);
    assertThat(regularCollectionArgument.check(driver,
      mockElement("My favorite fruit is of course bAnAnA")).verdict()).isEqualTo(REJECT);
    assertThat(regularCollectionArgument.check(driver,
      mockElement("bAnAnA My favorite fruit is of course")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldNotMatchRegularCollection() {
    assertThat(regularCollectionArgument.check(driver,
      mockElement("My favorite fruit is Banan of course")).verdict()).isEqualTo(REJECT);
  }

  @Test
  void shouldThrowExceptionBecauseOfNullCollectionItem() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfTextsCaseSensitive(collectionWithNull));
  }

  @Test
  void shouldThrowExceptionForEmptyCollection() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfTextsCaseSensitive(emptyList()));
  }

  @Test
  void shouldThrowExceptionIfCollectionContainsEmptyOrBlankString() {
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfTextsCaseSensitive(collectionWithEmptyString));
    assertThatIllegalArgumentException().isThrownBy(() -> new OneOfTextsCaseSensitive(collectionWithBlankString));
  }
}
