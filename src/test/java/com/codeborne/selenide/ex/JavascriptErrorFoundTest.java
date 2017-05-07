package com.codeborne.selenide.ex;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class JavascriptErrorFoundTest {

  @Test
  public void testConstructor() {
    JavaScriptErrorsFound javascriptErrorFound = new JavaScriptErrorsFound(Arrays.asList("Error1", "Error2", "Error3"));
    String expectedString = "JavaScriptErrorsFound JavaScript errors found\n" +
        "Screenshot: null\n" +
        "Javascript Errors: [Error1, Error2, Error3]\n" +
        "Timeout: 0 ms.";
    assertEquals(expectedString, javascriptErrorFound.toString());
  }

  @Test
  public void testGetJsErrorsFound() {
    List<String> jsErrors = Arrays.asList("Error1", "Error2", "Error3");
    JavaScriptErrorsFound javascriptErrorFound = new JavaScriptErrorsFound(jsErrors);
    assertEquals(jsErrors, javascriptErrorFound.getJsErrors());
  }
}
