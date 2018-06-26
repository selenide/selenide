package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HeadOfCollectionTest {
  private final WebElement element1 = mock(WebElement.class);
  private final WebElement element2 = mock(WebElement.class);
  private final WebElement element3 = mock(WebElement.class);
  private WebElementsCollection originalCollection = mock(WebElementsCollection.class);

  @BeforeEach
  void setUp() {
    when(originalCollection.getActualElements()).thenReturn(asList(element1, element2, element3));
  }

  @Test
  void lessThanOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 2);
    Assertions.assertEquals(asList(element1, element2), $$.getElements());
  }

  @Test
  void equalToOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 3);
    Assertions.assertEquals(asList(element1, element2, element3), $$.getElements());
  }

  @Test
  void greaterThanOriginalSize() {
    HeadOfCollection $$ = new HeadOfCollection(originalCollection, 4);
    Assertions.assertEquals(asList(element1, element2, element3), $$.getElements());
  }
}
