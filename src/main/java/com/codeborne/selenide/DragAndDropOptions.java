package com.codeborne.selenide;

import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.DEFAULT;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.JS;

public class DragAndDropOptions {

  private DragAndDropMethod method;

  public DragAndDropOptions(DragAndDropMethod method) {
    this.method = method;
  }

  public static DragAndDropOptions usingJavaScript() {
    return new DragAndDropOptions(JS);
  }

  public static DragAndDropOptions defaultStrategy() {
    return new DragAndDropOptions(DEFAULT);
  }

  public DragAndDropMethod getMethod() {
    return method;
  }

  public enum DragAndDropMethod {

    /**
     * Executing drag and drop via Selenium Actions
     */
    DEFAULT,

    /**
     * Executing drag and drop via JS script
     */
    JS
  }

  @Override
  public String toString() {
    return String.format("Drag and Drop method: %s", method);
  }
}

