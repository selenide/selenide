package com.codeborne.selenide;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClipboardDefault implements Clipboard {

  private Driver driver;

  public ClipboardDefault(Driver driver) {
    this.driver = driver;
  }

  public String getText() {
    assertRemoteState();
    String content = null;
    try {
      content = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
    } catch (UnsupportedFlavorException | IOException e) {
      throw new IllegalStateException("Can't get clipboard data! " + e.getMessage(), e);
    }
    return content;
  }

  public void setText(String text) {
    assertRemoteState();
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), new StringSelection(text));
  }

  public void shouldBeText(String text) {
    assertRemoteState();
    assertEquals(text, getText(), "Clipboard data doesn't match with expected!");
  }

  private void assertRemoteState() {
    if (driver.config().remote() != null)
      throw new IllegalStateException("Remote driver url detected! Please use remote clipboard.");
  }

}
