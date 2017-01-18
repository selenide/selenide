package com.codeborne.selenide.hookPreformers;

import org.openqa.selenium.WebElement;

public interface HookAction {
    /**
    *   Condition for activation
    */
    boolean conditionForAction(WebElement element, String methodName);

    /**
     *   Action when activated
     */
    void action(WebElement element, String methodName);
}
