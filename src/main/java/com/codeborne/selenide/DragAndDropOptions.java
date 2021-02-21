package com.codeborne.selenide;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.ACTIONS;
import static com.codeborne.selenide.DragAndDropOptions.DragAndDropMethod.JS;

@ParametersAreNonnullByDefault
public class DragAndDropOptions {

  private final DragAndDropMethod method;

  public DragAndDropOptions(DragAndDropMethod method) {
    this.method = method;
  }

  public static DragAndDropOptions usingJavaScript() {
    return new DragAndDropOptions(JS);
  }

  public static DragAndDropOptions usingActions() {
    return new DragAndDropOptions(ACTIONS);
  }

  public DragAndDropMethod getMethod() {
    return method;
  }

  public enum DragAndDropMethod {

    /**
     * Executing drag and drop via Selenium Actions
     */
    ACTIONS,

    /**
     * Executing drag and drop via JS script
     */
    JS
  }

  @Override
  public String toString() {
    return String.format("method: %s", method);
  }
}

