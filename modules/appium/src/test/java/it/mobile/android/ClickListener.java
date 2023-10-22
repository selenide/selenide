package it.mobile.android;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickListener implements WebDriverListener {
  private static final Logger log = LoggerFactory.getLogger(ClickListener.class);

  @Override
  public void beforeClick(WebElement element) {
    log.info("before click {}", element);
  }
}
