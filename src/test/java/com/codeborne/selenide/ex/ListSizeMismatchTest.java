package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListSizeMismatchTest implements WithAssertions {
  private String operator = "Operator";
  private int expectedSize = 10;
  private Driver driver = new DriverStub();
  private WebElementsCollection webElementsCollection = mock(WebElementsCollection.class);
  private List<WebElement> actualElementsList = asList(mock(WebElement.class),
    mock(WebElement.class),
    mock(WebElement.class));
  private Exception exception = new Exception("Exception message");
  private long timeoutMs = 1000L;

  @BeforeEach
  void setUp() {
    when(webElementsCollection.driver()).thenReturn(driver);
  }

  @Test
  void toString_withoutExplanation() {
    ListSizeMismatch listSizeMismatch = new ListSizeMismatch(driver, operator,
      expectedSize,
      null,
      webElementsCollection,
      actualElementsList,
      exception,
      timeoutMs);
    String expectedString = "ListSizeMismatch : expected: Operator 10, actual: 3, collection: null\n" +
      "Elements: [\n" +
      "\t<null displayed:false></null>,\n" +
      "\t<null displayed:false></null>,\n" +
      "\t<null displayed:false></null>\n" +
      "]\n" +
      "Screenshot: null\n" +
      "Timeout: 1 s.\n" +
      "Caused by: java.lang.Exception: Exception message";
    assertThat(listSizeMismatch)
      .hasToString(expectedString);
  }

  @Test
  void toString_withExplanation() {
    ListSizeMismatch listSizeMismatch = new ListSizeMismatch(driver, operator,
      expectedSize,
      "it's said in customer requirement #12345",
      webElementsCollection,
      actualElementsList,
      exception,
      timeoutMs);
    String expectedString = "ListSizeMismatch : expected: Operator 10" +
      " (because it's said in customer requirement #12345), actual: 3, collection: null\n" +
      "Elements: [\n" +
      "\t<null displayed:false></null>,\n" +
      "\t<null displayed:false></null>,\n" +
      "\t<null displayed:false></null>\n" +
      "]\n" +
      "Screenshot: null\n" +
      "Timeout: 1 s.\n" +
      "Caused by: java.lang.Exception: Exception message";
    assertThat(listSizeMismatch)
      .hasToString(expectedString);
  }
}
