package com.codeborne.selenide.hookPrefomers;

import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Set;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static java.util.Arrays.asList;

public class PresentationAction implements HookAction{
    private static final Set<String> presentationMethods = new HashSet<>(asList(
    "click",
    "contextClick",
    "doubleClick",
    "followLink"
    ));

    private int flashPause = 200;

    @Override
    public boolean isActive(WebElement element, String methodName) {
        return (presentationMethods.contains(methodName) && PresentationMode.active);
    }

    @Override
    public void action(WebElement element, String methodName) {

        wait(PresentationMode.delayBeforeCommand);

        String flasherId = "selenideFlasher";
        switch (methodName){
            case "doubleClick":
                for(int i=0; i < 2; i++){
                    markElement(element, PresentationMode.flashColor, flasherId);
                    wait(flashPause);
                    removeMarker(flasherId);
                    wait(flashPause);
                }
                break;

            default:
                markElement(element, PresentationMode.flashColor, flasherId);
                wait(flashPause);
                removeMarker(flasherId);
                break;
        }

        if (PresentationMode.markElements) markElement(element, PresentationMode.markColor, "selenideMarker");
    }



    private static void markElement(WebElement element, String elementColor, String elementId){
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

    private static void removeMarker(String elementId){
        executeJavaScript("if (document.contains(document.getElementById('" + elementId + "'))) {" +
                "document.getElementById('" + elementId + "').remove()}");
    }

    private static void wait(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
