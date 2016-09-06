package com.codeborne.selenide.commands;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class Flash implements Command<Void> {
    @Override
    public Void execute(SelenideElement proxy, WebElementSource locator, Object[] args) {
        flash(locator.findAndAssertElementIsVisible());
        return null;
    }

    String FLASHER_ID = "selenide_flasher";
    String FLASHER_COLOR = "#00FF00";

    protected void flash(WebElement element) {
        for(int i=0; i < 2; i++){
            showFlash(element);
            killFlash();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {}
        }
    }

    private void showFlash(WebElement element){
        executeJavaScript("var flasher = document.createElement('div');" +
                        "var parentOffsets = arguments[0].getBoundingClientRect();"+
                        "flasher.id = '" + FLASHER_ID + "';" +
                        "flasher.style.position = 'absolute';" +
                        "flasher.style.top = parentOffsets.top - 5 + 'px';" +
                        "flasher.style.left = parentOffsets.left - 5 + 'px';" +
                        "flasher.style.height = parentOffsets.height + 10 + 'px';" +
                        "flasher.style.width = parentOffsets.width + 10 + 'px';" +
                        "flasher.style.zIndex = 666;" +
                        "flasher.style.backgroundColor = '" + FLASHER_COLOR + "';" +
                        "flasher.style.opacity = '0.5';" +
                        "document.body.appendChild(flasher);",
                        element);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {}
    }

    private void killFlash(){
        executeJavaScript("if (document.contains(document.getElementById('" + FLASHER_ID + "'))) {" +
                "document.getElementById('" + FLASHER_ID + "').remove()}");
    }

}




