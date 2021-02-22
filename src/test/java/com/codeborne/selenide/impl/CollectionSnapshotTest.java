package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

final class CollectionSnapshotTest {

  private final CollectionSource originalCollection = mock(CollectionSource.class);

  @AfterEach
  void verifyNoMoreInteractionsOnOriginalCollection() {
    verify(originalCollection).getElements();
    verifyNoMoreInteractions(originalCollection);
  }

  @Test
  void getOriginalCollectionSnapshotElements() {
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);

    List<WebElement> originalCollectionElements = asList(mockedWebElement1, mockedWebElement2);
    when(originalCollection.getElements()).thenReturn(originalCollectionElements);

    CollectionSnapshot collectionSnapshot = new CollectionSnapshot(originalCollection);
    List<WebElement> snapshotCollectionElements = collectionSnapshot.getElements();

    assertThat(snapshotCollectionElements).isNotSameAs(originalCollectionElements);
    assertThat(snapshotCollectionElements).isEqualTo(originalCollectionElements);

    // Returns snapshot version nevertheless how many times we call getElements()
    List<WebElement> snapshotCollectionElements2 = collectionSnapshot.getElements();
    assertThat(snapshotCollectionElements2).isSameAs(snapshotCollectionElements);
    assertThat(snapshotCollectionElements2).isEqualTo(snapshotCollectionElements);
  }

  @Test
  void getOriginalCollectionSnapshotElement() {
    WebElement mockedWebElement = mock(WebElement.class);

    when(originalCollection.getElements()).thenReturn(singletonList(mockedWebElement));

    WebElement collectionElement = new CollectionSnapshot(originalCollection).getElement(0);
    assertThat(collectionElement).isEqualTo(mockedWebElement);
  }

  @Test
  void description() {
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);

    when(originalCollection.description()).thenReturn("Collection description");
    when(originalCollection.getElements()).thenReturn(asList(mockedWebElement1, mockedWebElement2));

    CollectionSnapshot collectionSnapshot = new CollectionSnapshot(originalCollection);
    assertThat(collectionSnapshot.description()).isEqualTo("Collection description.snapshot(2 elements)");
    // Call one more time to check that getElements() is executed only once
    assertThat(collectionSnapshot.description()).isEqualTo("Collection description.snapshot(2 elements)");

    verify(originalCollection, times(2)).description();
  }


  @Test
  void driver() {
    Driver driver = mock(Driver.class);

    when(originalCollection.driver()).thenReturn(driver);

    CollectionSnapshot collectionSnapshot = new CollectionSnapshot(originalCollection);
    assertThat(collectionSnapshot.driver()).isEqualTo(driver);

    verify(originalCollection).driver();
  }
}
