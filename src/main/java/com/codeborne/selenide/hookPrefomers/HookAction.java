package com.codeborne.selenide.hookPrefomers;

import org.openqa.selenium.WebElement;

public interface HookAction {
    boolean isActive(WebElement element, String methodName);
    void action(WebElement element, String methodName);
}
