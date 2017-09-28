package com.codeborne.selenide.hookperformers;

import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.sleep;

public class FlashAction extends MarkAction {

  private int flashPause = 200;

  @Override
  public boolean conditionForAction(WebElement element, String methodName, Object... args) {
    return (presentationMode.active && presentationMode.flashElements) && presentationMethods.contains(methodName);
  }

  @Override
  public void action(WebElement element, String methodName, Object... args) {

    String flasherId = "selenideFlasher";
    switch (methodName) {
      case "doubleClick":
        for (int i = 0; i < 2; i++) {
          markElement(element, presentationMode.flashColor, flasherId, presentationMode.flashStyle);
          sleep(flashPause);
          removeMarker(flasherId);
          sleep(flashPause);
        }
        break;
      default:
        markElement(element, presentationMode.flashColor, flasherId, presentationMode.flashStyle);
        sleep(flashPause);
        removeMarker(flasherId);
        break;
    }
  }

  private static void removeMarker(String elementId) {
    executeJavaScript("if (document.contains(document.getElementById('" + elementId + "'))) {" +
            "document.getElementById('" + elementId + "').remove()}");
  }
}
