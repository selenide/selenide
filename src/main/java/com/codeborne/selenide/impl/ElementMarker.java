package com.codeborne.selenide.impl;

import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class ElementMarker {

    public static void markElement(WebElement element, String color) {
        markElement(element, color, "selenide_marker");
    }

    public static void markElement(WebElement element, String element_color, String element_id){
        executeJavaScript("var flasher = document.createElement('div');" +
                        "var parentOffsets = arguments[0].getBoundingClientRect();"+
                        "flasher.id = '" + element_id + "';" +
                        "flasher.style.position = 'absolute';" +
                        "flasher.style.top = parentOffsets.top - 1 + 'px';" +
                        "flasher.style.left = parentOffsets.left - 1 + 'px';" +
                        "flasher.style.height = parentOffsets.height + 2 + 'px';" +
                        "flasher.style.width = parentOffsets.width + 2 + 'px';" +
                        "flasher.style.zIndex = 666;" +
                        "flasher.style.backgroundColor = '" + element_color + "';" +
                        "flasher.style.borderRadius = '5px';" +
                        "flasher.style.opacity = '0.5';" +
                        "document.body.appendChild(flasher);",
                element);
    }

    public static void removeMarker(String element_id){
        executeJavaScript("if (document.contains(document.getElementById('" + element_id + "'))) {" +
                "document.getElementById('" + element_id + "').remove()}");
    }

    public static void flashElement(WebElement element, String color){
        String flasher_id = "selenide_flasher";
        for(int i=0; i < 2; i++){
            markElement(element, color, flasher_id);
            wait(200);
            removeMarker(flasher_id);
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
}
