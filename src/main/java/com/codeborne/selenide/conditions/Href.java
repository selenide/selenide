package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public class Href extends AttributeWithValue {
  public Href(String expectedAttributeValue) {
    super("href", expectedAttributeValue);
  }

  @CheckReturnValue
  @Override
  public boolean apply(Driver driver, WebElement element) {
    String fullUrl = decode(getAttributeValue(element));
    return fullUrl.endsWith(expectedAttributeValue) ||
      fullUrl.endsWith(expectedAttributeValue + "/");
  }

  String decode(String url) {
    try {
      return URLDecoder.decode(url, UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Failed to decode " + url, e);
    }
  }
}
