package com.codeborne.selenide.webdriver;

import java.time.Duration;

/**
 * A temporary workaround to override default timeouts of NettyClient used in Selenium.
 *
 * Its default read timeout 3 minutes is too long.
 * Selenide sets to 1.5 minutes.
 *
 * @deprecated Not used anymore since Selenium replaced NettyClient by JDK HttpClient (Selenide 7.0.0).
 * Changing these fields has no effect.
 * For remote webdriver, use {@code Configuration.remoteReadTimeout} and {@code Configuration.remoteConnectionTimeout}.
 */
@Deprecated(forRemoval = true)
public class HttpClientTimeouts {
  public static Duration defaultLocalReadTimeout = Duration.ofSeconds(90);
  public static Duration defaultLocalConnectTimeout = Duration.ofSeconds(10);
}
