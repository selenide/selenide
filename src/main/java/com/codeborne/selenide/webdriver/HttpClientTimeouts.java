package com.codeborne.selenide.webdriver;

import java.time.Duration;

/**
 * A temporary workaround to override default timeouts of NettyClient used in Selenium.
 *
 * Its default read timeout 3 minutes is too long.
 * Selenide sets to 1.5 minutes.
 */
public class HttpClientTimeouts {
  public static Duration defaultLocalReadTimeout = Duration.ofSeconds(90);
  public static Duration defaultLocalConnectTimeout = Duration.ofSeconds(10);
}
