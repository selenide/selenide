package com.codeborne.selenide.hookPrefomers;

import org.openqa.selenium.WebElement;

import java.util.ArrayList;

public class BeforeHookPrefomer {
    private static BeforeHookPrefomer instance;
    private ArrayList<HookAction> actions = new ArrayList<>();

    private BeforeHookPrefomer() {}

    public static BeforeHookPrefomer getInstance() {
        if (instance == null){
            instance = new BeforeHookPrefomer();
            instance.actions.add(new PresentationAction()); // add default action
        }
        return instance;
    }

    public void prefom(WebElement element, String methodName){
        for (HookAction action: this.actions){
            if (action.isActive(element,methodName)) action.action(element, methodName);
        }
    }
}
