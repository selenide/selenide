package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

public class ListSizeMismatchTest {

  @Test
  public void testToString() {
    String operator = "Operator";
    int expectedSize = 10;
    WebElementsCollection webElementsCollection = mock(WebElementsCollection.class);
    List<WebElement> actualElementsList = asList(mock(WebElement.class),
        mock(WebElement.class),
        mock(WebElement.class));
    Exception exception = new Exception("Exception message");
    long timeoutMs = 1000L;
    ListSizeMismatch listSizeMismatch = new ListSizeMismatch(operator,
        expectedSize,
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
    assertEquals(expectedString, listSizeMismatch.toString());
  }
}
