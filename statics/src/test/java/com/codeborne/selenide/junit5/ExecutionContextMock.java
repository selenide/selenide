package com.codeborne.selenide.junit5;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ExecutionContextMock implements ExtensionContext {


  @Override
  public Optional<ExtensionContext> getParent() {
    return Optional.empty();
  }

  @Override
  public ExtensionContext getRoot() {
    return null;
  }

  @Override
  public String getUniqueId() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public Set<String> getTags() {
    return null;
  }

  @Override
  public Optional<AnnotatedElement> getElement() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<?>> getTestClass() {
    return Optional.empty();
  }

  @Override
  public Optional<TestInstance.Lifecycle> getTestInstanceLifecycle() {
    return Optional.empty();
  }

  @Override
  public Optional<Object> getTestInstance() {
    return Optional.empty();
  }

  @Override
  public Optional<TestInstances> getTestInstances() {
    return Optional.empty();
  }

  @Override
  public Optional<Method> getTestMethod() {
    return Optional.of(this.getClass().getMethods()[0]);
  }

  @Override
  public Optional<Throwable> getExecutionException() {
    return Optional.of(new RuntimeException());
  }

  @Override
  public Optional<String> getConfigurationParameter(String key) {
    return Optional.empty();
  }

  @Override
  public void publishReportEntry(Map<String, String> map) {

  }

  @Override
  public Store getStore(Namespace namespace) {
    return null;
  }

}
