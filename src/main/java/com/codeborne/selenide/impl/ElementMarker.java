package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Configuration.markElementsMode;
import static com.codeborne.selenide.Configuration.slowAndFlashMode;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class ElementMarker {
    private static String RED = "#FF0000";
    private static String GREEN = "#00FF00";

    public static void markElement(WebElement element, String color) {
        markElement(element, color, "selenideMarker");
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

    public static void flashElement(WebElement element, String color){
        String flasherId = "selenideFlasher";
        for(int i=0; i < 2; i++){
            markElement(element, color, flasherId);
            wait(200);
            removeMarker(flasherId);
            wait(200);
        }
    }

    private static void wait(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    static void flashElementIfNeeded(WebElement element, String methodName) {
        if (slowAndFlashMode && !methodName.equals("flash")){
            flashElement(element, GREEN);
            wait(200);
        }
    }

    static void markGreenElementIfNeeded(WebElement element, String methodName) {
        if (markElementsMode && !methodName.equals("mark")){
            markElement(element, GREEN);
        }
    }

    static void markElementRedIfNeeded(WebElement webElement, String methodName) {
        if (markElementsMode && !methodName.equals("mark")) {
            markElement(webElement, RED);
        }
    }
}
