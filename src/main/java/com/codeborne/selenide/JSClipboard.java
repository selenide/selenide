package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Map;

@ParametersAreNonnullByDefault
public class JSClipboard implements Clipboard {
  private final Driver driver;

  public JSClipboard(Driver driver) {
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
    Map<String, Object> result = driver.executeAsyncJavaScript(
      """
          const callback = arguments[arguments.length - 1];
          navigator.clipboard.readText()
            .then(text => {
              console.log('Pasted content: ', text);
              callback({result: text, error: null});
            })
            .catch(err => {
              console.error('Failed to read clipboard contents: ', err);
              callback({result: null, error: err});
            });
        """
      // "return navigator.clipboard.readText()"
    );
    // error.code = 0
    // error.message -> Read permission denied.
    // error.name -> NotAllowedError

    @SuppressWarnings("unchecked")
    Map<String, Object> error = (Map<String, Object>) result.get("error");
    if (error != null) {
      throw new IllegalArgumentException("Failed to get clipboard text. Caused by: " + error.get("message"));
    }
    return (String) result.get("result");
  }

  @Override
  public void setText(String text) {
    Map<String, Object> result = driver.executeAsyncJavaScript(
      """
          const callback = arguments[arguments.length - 1];
          const text = arguments[0];
          navigator.clipboard.writeText(text)
            .then(text => {
              console.log('Pasted content: ', text);
              callback({result: text, error: null});
            })
            .catch(err => {
              console.error('Failed to write clipboard contents: ', err);
              callback({result: null, error: err});
            });
        """, text
    );
    @SuppressWarnings("unchecked")
    Map<String, Object> error = (Map<String, Object>) result.get("error");
    if (error != null) {
      throw new IllegalArgumentException("Failed to set clipboard text. Caused by: " + error.get("message"));
    }
    System.out.println("result=" + result);
  }
}
