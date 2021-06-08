package org.selenide.selenoid;

import com.codeborne.selenide.Clipboard;
import com.codeborne.selenide.DefaultClipboard;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;


public class SelenoidClipboard implements Clipboard {

    private Driver driver;
    private static final Logger log = LoggerFactory.getLogger(SelenoidClipboard.class);

    public SelenoidClipboard(Driver driver) {
        this.driver = driver;
    }

    @Nonnull
    @Override
    public String getText() {
        if (driver.config().remote() == null) {
            log.debug("Working in local browser. Switching to a default Clipboard implementation.");
            return new DefaultClipboard(driver).getText();
        } else {
            return new SelenoidClient(driver.config().remote(), driver.getSessionId().toString()).getClipboardText();
        }
    }

    @Override
    public void setText(String text) {
        if (driver.config().remote() == null) {
            log.debug("Working in local browser. Switching to a default Clipboard implementation.");
            new DefaultClipboard(driver).setText(text);
        } else {
            new SelenoidClient(driver.config().remote(), driver.getSessionId().toString()).setClipboardText(text);
        }
    }
}
