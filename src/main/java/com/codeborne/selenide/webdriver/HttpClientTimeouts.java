package com.codeborne.selenide.webdriver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

/**
 * A temporary workaround to override default timeouts of NettyClient used in Selenium.
 *
 * Its default read timeout 3 minutes is too long.
 * Selenide sets to 1.5 minutes.
 *
 * @since 5.22.0
 */
@ParametersAreNonnullByDefault
class HttpClientTimeouts {
  public static Duration defaultReadTimeout = Duration.ofSeconds(90);
}
