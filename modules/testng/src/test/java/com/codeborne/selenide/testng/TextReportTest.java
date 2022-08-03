package com.codeborne.selenide.testng;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class TextReportTest {
  private final TextReport listener = new TextReport();

  @Test
  void classesMarkedWith_TextReport_shouldGeneratedReport() {
    assertThat(listener.isClassAnnotatedWithReport(BaseTestWithTextReport.class)).isTrue();
    assertThat(listener.isClassAnnotatedWithReport(BaseTestWithGlobalTextReport.class)).isTrue();
    assertThat(listener.isClassAnnotatedWithReport(TestWithCustomTextReport.class)).isTrue();
  }

  @Test
  void classesNotMarkedWith_TextReport_shouldNotGeneratedReport() {
    assertThat(listener.isClassAnnotatedWithReport(AnotherTestWithoutTextReport.class)).isFalse();
  }

  @Test
  void allChildrenClassesInherit_TextReport() {
    assertThat(listener.isClassAnnotatedWithReport(SomeTestWithTextReport.class)).isTrue();
    assertThat(listener.isClassAnnotatedWithReport(TestWithOwnListeners.class)).isTrue();
  }

  @Test
  void allChildrenClassesInherit_GlobalTextReport() {
    assertThat(listener.isClassAnnotatedWithReport(SomeTestWithGlobalTextReport.class)).isTrue();
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

  @Listeners(GlobalTextReport.class)
  private abstract static class BaseTestWithGlobalTextReport {
  }

  private abstract static class SomeTestWithGlobalTextReport extends BaseTestWithGlobalTextReport {
  }

  private abstract static class AnotherTestWithoutTextReport {
  }

  private static class CustomTextReport extends TextReport {
  }
}
