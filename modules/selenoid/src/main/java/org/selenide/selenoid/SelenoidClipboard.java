package org.selenide.selenoid;

import com.codeborne.selenide.DefaultClipboard;
import com.codeborne.selenide.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.selenide.selenoid.SelenoidClient.clientFor;

@ParametersAreNonnullByDefault
public class SelenoidClipboard extends DefaultClipboard {
  private static final Logger log = LoggerFactory.getLogger(SelenoidClipboard.class);

  public SelenoidClipboard(Driver driver) {
    super(driver);
  }

  @CheckReturnValue
  @Nonnull
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

  @CheckReturnValue
  private boolean isLocalWebdriver() {
    return driver().config().remote() == null;
  }
}
