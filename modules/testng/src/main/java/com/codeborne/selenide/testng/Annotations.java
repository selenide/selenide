package com.codeborne.selenide.testng;

import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class Annotations {
  @Nullable
  @SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
  static <T extends Annotation> T annotation(Class<?> testClass, Class<T> annotationClass) {
    return testClass.getAnnotation(annotationClass);
  }

  @Nullable
  @SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
  static <T extends Annotation> T annotation(Method testMethod, Class<T> annotationClass) {
    return testMethod.getAnnotation(annotationClass);
  }
}
