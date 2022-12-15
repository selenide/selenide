package com.codeborne.selenide.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class TailOfCollectionTest {
  private final WebElement element1 = mock();
  private final WebElement element2 = mock();
  private final WebElement element3 = mock();
  private final CollectionSource originalCollection = mock();

  @BeforeEach
  void setUp() {
    when(originalCollection.description()).thenReturn("li.active");
    when(originalCollection.getElements()).thenReturn(asList(element1, element2, element3));
  }

  @Test
  void lessThanOriginalSize() {
    TailOfCollection $$ = new TailOfCollection(originalCollection, 2);
    assertThat($$.getElements())
      .isEqualTo(asList(element2, element3));
  }

  @Test
  void equalToOriginalSize() {
    TailOfCollection $$ = new TailOfCollection(originalCollection, 3);
    assertThat($$.getElements())
      .isEqualTo(asList(element1, element2, element3));
  }

  @Test
  void greaterThanOriginalSize() {
    TailOfCollection $$ = new TailOfCollection(originalCollection, 4);
    assertThat($$.getElements())
      .isEqualTo(asList(element1, element2, element3));
  }

  @Test
  void description() {
    TailOfCollection $$ = new TailOfCollection(originalCollection, 4);
    assertThat($$.description())
      .isEqualTo("li.active:last(4)");
  }
}
