package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.impl.ElementMarker.*;

public class Mark implements Command<Void> {

    protected String markerColor;

    public Mark() {
        this("#FF0000");
    }

    protected Mark(String color) {
        this.markerColor = color;
    }

    @Override
    public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
        if (args != null) {
            markerColor = (String) args[0];
        }
        mark(locator.findAndAssertElementIsVisible());
        return null;
    }

    protected void mark(WebElement element){
        markElement(element, markerColor);
    }
}