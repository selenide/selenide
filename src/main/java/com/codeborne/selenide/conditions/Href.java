package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public class Href extends AttributeWithValue {
  public Href(String expectedAttributeValue) {
    super("href", expectedAttributeValue);
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String href = getAttributeValue(element);
    String fullUrl = decode(href);
    boolean matches = fullUrl.endsWith(expectedAttributeValue) ||
      fullUrl.endsWith(expectedAttributeValue + "/") ||
      href.endsWith(expectedAttributeValue);
    return new CheckResult(matches, String.format("href=\"%s\"", href));
  }

  String decode(String url) {
    try {
      return URLDecoder.decode(url, UTF_8.name());
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Failed to decode " + url, e);
    }
  }
}
