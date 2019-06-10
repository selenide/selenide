package com.codeborne.selenide.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;

public class ScreenShooterExtensionMock extends ScreenShooterExtension {
  int counter = 0;

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    super.testFailed(context, cause);
    counter++;
  }

  public int getCounter() {
    return counter;
  }
}
