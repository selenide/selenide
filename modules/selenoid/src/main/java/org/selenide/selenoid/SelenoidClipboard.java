package org.selenide.selenoid;

import com.codeborne.selenide.DefaultClipboard;
import com.codeborne.selenide.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.selenide.selenoid.SelenoidClient.clientFor;

public class SelenoidClipboard extends DefaultClipboard {
  private static final Logger log = LoggerFactory.getLogger(SelenoidClipboard.class);

  public SelenoidClipboard(Driver driver) {
    super(driver);
  }

  @Override
  public String getText() {
    if (isLocalWebdriver()) {
      log.debug("Working in local browser. Switching to a default Clipboard implementation.");
      return super.getText();
    }
    else {
      return clientFor(driver()).getClipboardText();
    }
  }

  @Override
  public void setText(String text) {
    if (isLocalWebdriver()) {
      log.debug("Working in local browser. Switching to a default Clipboard implementation.");
      super.setText(text);
    }
    else {
      clientFor(driver()).setClipboardText(text);
    }
  }

  private boolean isLocalWebdriver() {
    return driver().config().remote() == null;
  }
}
