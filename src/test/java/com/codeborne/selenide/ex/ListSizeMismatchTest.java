package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ListSizeMismatchTest {

  @Test
  public void testToString() {
    String operator = "Operator";
    int expectedSize = 10;
    WebElementsCollection webElementsCollection = Mockito.mock(WebElementsCollection.class);
    List<WebElement> actualElementsList = Arrays.asList(Mockito.mock(WebElement.class),
                                                        Mockito.mock(WebElement.class),
                                                        Mockito.mock(WebElement.class));
    Exception exception = new Exception("Exception message");
    long timeoutMs = 1000L;
    ListSizeMismatch listSizeMismatch = new ListSizeMismatch(operator, expectedSize, webElementsCollection, actualElementsList, exception, timeoutMs);
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
