package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SnapshotCollectionTest {

  private CollectionSource mockedCollection = mock(CollectionSource.class);

  @AfterEach
  void verifyNoMoreInteractions() {
    verify(mockedCollection).getElements();
    Mockito.verifyNoMoreInteractions(mockedCollection);
  }

  @Test
  void getOriginalCollectionSnapshotElements() {
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);

    List<WebElement> originalCollectionElements = Arrays.asList(mockedWebElement1, mockedWebElement2);
    when(mockedCollection.getElements()).thenReturn(originalCollectionElements);

    SnapshotCollection snapshotCollection = new SnapshotCollection(mockedCollection);
    List<WebElement> snapshotCollectionElements = snapshotCollection.getElements();

    assertThat(snapshotCollectionElements).isNotSameAs(originalCollectionElements);
    assertThat(snapshotCollectionElements).isEqualTo(originalCollectionElements);

    // Returns snapshot version nevertheless how many times we call getElements()
    List<WebElement> snapshotCollectionElements2 = snapshotCollection.getElements();
    assertThat(snapshotCollectionElements2).isSameAs(snapshotCollectionElements);
    assertThat(snapshotCollectionElements2).isEqualTo(snapshotCollectionElements);
  }

  @Test
  void getOriginalCollectionSnapshotElement() {
    WebElement mockedWebElement = mock(WebElement.class);

    when(mockedCollection.getElements()).thenReturn(Arrays.asList(mockedWebElement));

    WebElement collectionElement = new SnapshotCollection(mockedCollection).getElement(0);
    assertThat(collectionElement).isEqualTo(mockedWebElement);
  }

  @Test
  void description() {
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);

    when(mockedCollection.description()).thenReturn("Collection description");
    when(mockedCollection.getElements()).thenReturn(Arrays.asList(mockedWebElement1, mockedWebElement2));

    SnapshotCollection snapshotCollection = new SnapshotCollection(mockedCollection);
    assertThat(snapshotCollection.description()).isEqualTo("Collection description.snapshot(2 elements)");
    // Call one more time to check that getElements() is executed only once
    assertThat(snapshotCollection.description()).isEqualTo("Collection description.snapshot(2 elements)");

    verify(mockedCollection, times(2)).description();
  }


  @Test
  void driver() {
    Driver driver = mock(Driver.class);

    when(mockedCollection.driver()).thenReturn(driver);

    SnapshotCollection snapshotCollection = new SnapshotCollection(mockedCollection);
    assertThat(snapshotCollection.driver()).isEqualTo(driver);

    verify(mockedCollection).driver();
  }
}
