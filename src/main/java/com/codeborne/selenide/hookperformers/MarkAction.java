package com.codeborne.selenide.hookperformers;

import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Configuration.presentationMode;
import static com.codeborne.selenide.Configuration.presentationMode.BoxStyle.FILL;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static java.util.Arrays.asList;

public class MarkAction implements HookAction {
  protected static final Set<String> presentationMethods = new HashSet<>(asList(
          "click",
          "contextClick",
          "doubleClick",
          "followLink"
  ));

  @Override
  public boolean conditionForAction(WebElement element, String methodName, Object... args) {
    return (presentationMode.active && presentationMode.markElements) && presentationMethods.contains(methodName);
  }

  @Override
  public void action(WebElement element, String methodName, Object... args) {
    markElement(element, presentationMode.markColor, "selenideMarker", presentationMode.markStyle);
  }

  protected static void markElement(WebElement element, String elementColor, String elementId, presentationMode.BoxStyle style) {

    executeJavaScript("var flasher = document.createElement('div');" +
                    "var parentOffsets = arguments[0].getBoundingClientRect();" +
                    "flasher.id = '" + elementId + "';" +
                    "flasher.style.position = 'absolute';" +
                    "flasher.style.top = parentOffsets.top - 3 + 'px';" +
                    "flasher.style.left = parentOffsets.left - 3 + 'px';" +
                    "flasher.style.height = parentOffsets.height + 'px';" +
                    "flasher.style.width = parentOffsets.width  + 'px';" +
                    "flasher.style.zIndex = 666;" +
                    "flasher.style.border = 'solid';" +
                    "flasher.style.borderColor = '" + elementColor + "';" +
                    ((style == FILL) ? "flasher.style.backgroundColor = '" + elementColor + "';" : "") +
                    "flasher.style.borderRadius = '5px';" +
                    "flasher.style.opacity = '0.5';" +
                    "document.body.appendChild(flasher);",
            element);
  }

}
