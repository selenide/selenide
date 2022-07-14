package com.codeborne.selenide.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LazyCollectionSnapshotTest {

  private final CollectionSource collectionSource = mock(CollectionSource.class);

  @BeforeEach
  void setUp() {
    when(collectionSource.getElements()).thenAnswer(invocation -> Lists.list(
      mock(WebElement.class),
      mock(WebElement.class))
    );
  }

  @Test
  void testShouldCacheAllElements() {
    // Given
    LazyCollectionSnapshot lazyCollectionSnapshot = new LazyCollectionSnapshot(collectionSource);

    // When
    List<WebElement> elements1 = lazyCollectionSnapshot.getElements();
    List<WebElement> elements2 = lazyCollectionSnapshot.getElements();

    //Then
    assertThat(elements1).isNotEmpty().isSameAs(elements2);
    verify(collectionSource, times(1)).getElements();
  }

  @Test
  void testShouldCacheIndexedElement() {
    // Given
    LazyCollectionSnapshot lazyCollectionSnapshot = new LazyCollectionSnapshot(collectionSource);

    // When
    WebElement element1 = lazyCollectionSnapshot.getElement(0);
    WebElement element2 = lazyCollectionSnapshot.getElement(0);

    // Then
    assertThat(element1).isNotNull().isSameAs(element2);
    verify(collectionSource, times(1)).getElements();
  }

  @Test
  void testShouldNotCacheDifferentIndexedElements() {
    // Given
    LazyCollectionSnapshot lazyCollectionSnapshot = new LazyCollectionSnapshot(collectionSource);

    // When
    WebElement element1 = lazyCollectionSnapshot.getElement(0);
    WebElement element2 = lazyCollectionSnapshot.getElement(1);

    // Then
    assertThat(element1).isNotNull().isNotSameAs(element2);
    verify(collectionSource, times(1)).getElements();
  }
}
