package com.codeborne.selenide.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

public class HttpHelper {

  private final Pattern pattern = Pattern.compile(".*filename\\*?=\"?((.+)'')?([^\";?]*)\"?(;charset=(.*))?.*", CASE_INSENSITIVE);

  public Optional<String> getFileNameFromContentDisposition(String headerName, String headerValue) {
    if (!"Content-Disposition".equalsIgnoreCase(headerName) || headerValue == null) {
      return Optional.empty();
    }
    Matcher regex = pattern.matcher(headerValue);
    if (!regex.matches()) {
      return Optional.empty();
    }
    String fileName = regex.replaceFirst("$3");
    String encoding = defaultIfEmpty(regex.replaceFirst("$2"), regex.replaceFirst("$5"));
    return Optional.of(decodeHttpHeader(fileName, encoding));
  }

  private String decodeHttpHeader(String encoded, String encoding) {
    try {
      return URLDecoder.decode(encoded, defaultIfEmpty(encoding, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
