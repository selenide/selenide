package com.codeborne.selenide.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class HeadOfCollectionTest {
  private final WebElement element1 = mock();
  private final WebElement element2 = mock();
  private final WebElement element3 = mock();
  private final CollectionSource originalCollection = mock();

  @BeforeEach
  void setUp() {
    when(originalCollection.getElements()).thenReturn(asList(element1, element2, element3));
  }

  @Test
  void lessThanOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 2);
    assertThat($$.getElements())
      .isEqualTo(asList(element1, element2));
  }

  @Test
  void equalToOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 3);
    assertThat($$.getElements())
      .isEqualTo(asList(element1, element2, element3));
  }

  @Test
  void greaterThanOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 4);
    assertThat($$.getElements())
      .isEqualTo(asList(element1, element2, element3));
  }
}
