package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class DefaultClipboard implements Clipboard {
  private final Driver driver;

  public DefaultClipboard(Driver driver) {
    this.driver = driver;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public Driver driver() {
    return driver;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public Clipboard object() {
    return this;
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public String getText() {
    assertRemoteState();
    try {
      return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
    }
    catch (UnsupportedFlavorException | IOException e) {
      throw new IllegalStateException("Can't get clipboard data! " + e.getMessage(), e);
    }
  }

  @Override
  public void setText(String text) {
    assertRemoteState();
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), new StringSelection(text));
  }

  private void assertRemoteState() {
    if (driver.config().remote() != null)
      throw new IllegalStateException("Remote driver url detected! Please use remote clipboard.");
  }
}
