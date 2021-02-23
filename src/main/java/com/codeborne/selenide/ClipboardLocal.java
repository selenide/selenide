package com.codeborne.selenide;


import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClipboardLocal implements Clipboard {

  @Nonnull
  @CheckReturnValue
  public String getText() {
    String content = null;
    try {
      content = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
    } catch (UnsupportedFlavorException | IOException e) {
      throw new IllegalStateException("Can't get clipboard data! " + e);
    }
    return content;
  }

  public void setValue(String text) {
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), new StringSelection(text));
  }

  public void shouldBeText(String text) {
    assertEquals(text, getText(), "Clipboard data doesn't match with expected!");
  }

}
