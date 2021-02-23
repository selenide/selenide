package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Clipboard {

  private Driver driver;

  public Clipboard() {
  }

  public Clipboard(Driver driver) {
    this.driver = driver;
  }

  /**
   * Get text from clipboard
   *
   * @return
   */
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

  /**
   * Set value to clipboard
   *
   * @param text value to be set to clipboard
   */
  public void setValue(String text) {
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), new StringSelection(text));
  }

  /**
   * Check that value in clipboard equals to expected
   *
   * @param text expected value for compare
   */
  public void shouldBeText(String text) {
    assertEquals(text, getText(), "Clipboard data doesn't match with expected!");
  }

}
