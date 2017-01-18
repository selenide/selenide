package com.codeborne.selenide.hookPreformers;

import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class FlashAction extends MarkAction{

    private int flashPause = 200;

    @Override
    public boolean conditionForAction(WebElement element, String methodName) {
        return (presentationMethods.contains(methodName) && PresentationMode.active && PresentationMode.flashElements);
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
    }

    private static void removeMarker(String elementId){
        executeJavaScript("if (document.contains(document.getElementById('" + elementId + "'))) {" +
                "document.getElementById('" + elementId + "').remove()}");
    }
}
