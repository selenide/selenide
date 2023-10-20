package com.codeborne.selenide.testng;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public final class TextReportTest {
  private final TextReport listener = new TextReport();

  @Test
  void classesMarkedWith_TextReport_shouldGeneratedReport() {
    assertTrue(listener.isClassAnnotatedWithReport(BaseTestWithTextReport.class));
    assertTrue(listener.isClassAnnotatedWithReport(TestWithCustomTextReport.class));
  }

  @Test
  void classesNotMarkedWith_TextReport_shouldNotGeneratedReport() {
    assertFalse(listener.isClassAnnotatedWithReport(AnotherTestWithoutTextReport.class));
  }

  @Test
  void allChildrenClassesInherit_TextReport() {
    assertTrue(listener.isClassAnnotatedWithReport(SomeTestWithTextReport.class));
    assertTrue(listener.isClassAnnotatedWithReport(TestWithOwnListeners.class));
  }

  @Listeners(TextReport.class)
  private abstract static class BaseTestWithTextReport {
  }

  private abstract static class SomeTestWithTextReport extends BaseTestWithTextReport {
  }

  @Listeners(SoftAsserts.class)
  private abstract static class TestWithOwnListeners extends BaseTestWithTextReport {
  }

  @Listeners(CustomTextReport.class)
  private abstract static class TestWithCustomTextReport {
  }

  private abstract static class AnotherTestWithoutTextReport {
  }

  private static class CustomTextReport extends TextReport {
  }
}
