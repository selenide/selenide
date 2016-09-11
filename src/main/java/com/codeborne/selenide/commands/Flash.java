package com.codeborne.selenide.commands;

import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.impl.ElementMarker.*;

public class Flash extends Mark {

    public Flash(){super("#00FF00");}

    @Override
    protected void mark(WebElement element) {
        flashElement(element, markerColor);
    }


}
