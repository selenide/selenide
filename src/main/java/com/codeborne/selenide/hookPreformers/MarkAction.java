package com.codeborne.selenide.hookPreformers;

import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Configuration.PresentationMode;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static java.util.Arrays.asList;

public class MarkAction implements HookAction{
    protected static final Set<String> presentationMethods = new HashSet<>(asList(
    "click",
    "contextClick",
    "doubleClick",
    "followLink"
    ));

    @Override
    public boolean conditionForAction(WebElement element, String methodName) {
        return (presentationMethods.contains(methodName) && PresentationMode.active && PresentationMode.markElements);
    }

    @Override
    public void action(WebElement element, String methodName) {
        wait(PresentationMode.delayBeforeCommand);
        markElement(element, PresentationMode.markColor, "selenideMarker");
    }

    protected static void markElement(WebElement element, String elementColor, String elementId){
        executeJavaScript("var flasher = document.createElement('div');" +
                        "var parentOffsets = arguments[0].getBoundingClientRect();"+
                        "flasher.id = '" + elementId + "';" +
                        "flasher.style.position = 'absolute';" +
                        "flasher.style.top = parentOffsets.top - 1 + 'px';" +
                        "flasher.style.left = parentOffsets.left - 1 + 'px';" +
                        "flasher.style.height = parentOffsets.height + 2 + 'px';" +
                        "flasher.style.width = parentOffsets.width + 2 + 'px';" +
                        "flasher.style.zIndex = 666;" +
                        "flasher.style.backgroundColor = '" + elementColor + "';" +
                        "flasher.style.borderRadius = '5px';" +
                        "flasher.style.opacity = '0.5';" +
                        "document.body.appendChild(flasher);",
                element);
    }

    protected static void wait(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
