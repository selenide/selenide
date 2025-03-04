package com.codeborne.selenide;

import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v133.browser.Browser;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.impl.WebdriverUnwrapper.cast;
import static com.codeborne.selenide.impl.WebdriverUnwrapper.instanceOf;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static org.openqa.selenium.devtools.v133.browser.model.PermissionType.CLIPBOARDREADWRITE;
import static org.openqa.selenium.devtools.v133.browser.model.PermissionType.CLIPBOARDSANITIZEDWRITE;

public class DefaultClipboard implements Clipboard {
  private final Driver driver;

  public DefaultClipboard(Driver driver) {
    this.driver = driver;
  }

  @Override
  public Driver driver() {
    return driver;
  }

  @Override
  public Clipboard object() {
    return this;
  }

  private boolean grantPermission() {
    Optional<HasDevTools> cdpBrowser = cast(driver, HasDevTools.class);
    if (cdpBrowser.isPresent() && instanceOf(driver, ChromiumDriver.class)) {
      DevTools devTools = cdpBrowser.get().getDevTools();
      devTools.send(Browser.grantPermissions(List.of(CLIPBOARDREADWRITE, CLIPBOARDSANITIZEDWRITE), empty(), empty()));
      return true;
    }
    return false;
  }

  @Override
  public String getText() {
    if (grantPermission()) {
      return requireNonNull(driver.executeJavaScript("return await navigator.clipboard.readText()"));
    }
    try {
      assertLocalBrowser();
      return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
    }
    catch (UnsupportedFlavorException | IOException e) {
      throw new IllegalStateException("Can't get clipboard data! " + e.getMessage(), e);
    }
  }

  @Override
  public void setText(String text) {
    if (grantPermission()) {
      driver.executeJavaScript("await navigator.clipboard.writeText(arguments[0])", text);
    }
    else {
      assertLocalBrowser();
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), new StringSelection(text));
    }
  }

  private void assertLocalBrowser() {
    if (driver.config().remote() != null)
      throw new IllegalStateException("Remote driver url detected! Please use remote clipboard.");
  }
}
