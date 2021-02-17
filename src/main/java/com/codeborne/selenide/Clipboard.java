package com.codeborne.selenide;


import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class Clipboard {

  @Nonnull
  @CheckReturnValue
  public String getString() {
    String content = null;
    try {
      content = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
    } catch (UnsupportedFlavorException | IOException e) {
      throw new IllegalStateException("Can't get clipboard data! " + e);
    }
    return content;
  }

}
