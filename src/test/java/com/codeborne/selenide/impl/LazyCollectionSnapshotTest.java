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

  private final WebElement element1 = mock();
  private final WebElement element2 = mock();
  private final CollectionSource collectionSource = mock();

  @BeforeEach
  void setUp() {
    when(collectionSource.getElements()).thenReturn(List.of(element1, element2));
  }

  @Test
  void shouldCacheAllElements() {
    // Given
    LazyCollectionSnapshot lazyCollectionSnapshot = new LazyCollectionSnapshot(collectionSource);

    // When
    List<WebElement> elements1 = lazyCollectionSnapshot.getElements();
    List<WebElement> elements2 = lazyCollectionSnapshot.getElements();

    //Then
    assertThat(elements1).isSameAs(elements2);
    assertThat(elements1).containsExactly(element1, element2);
    verify(collectionSource, times(1)).getElements();
  }

  @Test
  void shouldCacheIndexedElement() {
    // Given
    LazyCollectionSnapshot lazyCollectionSnapshot = new LazyCollectionSnapshot(collectionSource);

    // When
    WebElement result1 = lazyCollectionSnapshot.getElement(0);
    WebElement result2 = lazyCollectionSnapshot.getElement(0);

    // Then
    assertThat(result1).isSameAs(result2);
    assertThat(result1).isSameAs(element1);
    verify(collectionSource, times(1)).getElements();
  }

  @Test
  void shouldNotCacheDifferentIndexedElements() {
    // Given
    LazyCollectionSnapshot lazyCollectionSnapshot = new LazyCollectionSnapshot(collectionSource);

    // When
    WebElement result1 = lazyCollectionSnapshot.getElement(0);
    WebElement result2 = lazyCollectionSnapshot.getElement(1);

    // Then
    assertThat(result1).isNotSameAs(result2);
    assertThat(result1).isSameAs(element1);
    assertThat(result2).isSameAs(element2);
    verify(collectionSource, times(1)).getElements();
  }
}
