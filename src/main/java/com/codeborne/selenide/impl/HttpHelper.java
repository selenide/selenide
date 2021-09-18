package com.codeborne.selenide.impl;

import org.apache.commons.io.FilenameUtils;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.left;

@ParametersAreNonnullByDefault
public class HttpHelper {

  private static final Pattern FILENAME_IN_CONTENT_DISPOSITION_HEADER =
    Pattern.compile(".*filename\\*?=\"?((.+)'')?([^\";?]*)\"?(;charset=(.*))?.*", CASE_INSENSITIVE);

  private static final Pattern FILENAME_FORBIDDEN_CHARACTERS =
    Pattern.compile("[#%&{}/\\\\<>*?$!'\":@+`|=]");

  @CheckReturnValue
  @Nonnull
  public Optional<String> getFileNameFromContentDisposition(Map<String, String> headers) {
    return getFileNameFromContentDisposition(headers.entrySet());
  }

  @CheckReturnValue
  @Nonnull
  public Optional<String> getFileNameFromContentDisposition(Collection<Map.Entry<String, String>> headers) {
    for (Map.Entry<String, String> header : headers) {
      Optional<String> fileName = getFileNameFromContentDisposition(header.getKey(), header.getValue());
      if (fileName.isPresent()) {
        return fileName;
      }
    }
    return Optional.empty();
  }

  @CheckReturnValue
  @Nonnull
  public Optional<String> getFileNameFromContentDisposition(String headerName, @Nullable String headerValue) {
    if (!"Content-Disposition".equalsIgnoreCase(headerName) || headerValue == null) {
      return Optional.empty();
    }
    Matcher regex = FILENAME_IN_CONTENT_DISPOSITION_HEADER.matcher(headerValue);
    if (!regex.matches()) {
      return Optional.empty();
    }
    String fileName = regex.replaceFirst("$3");
    String encoding = defaultIfEmpty(regex.replaceFirst("$2"), regex.replaceFirst("$5"));
    return Optional.of(decodeHttpHeader(fileName, encoding));
  }

  @CheckReturnValue
  @Nonnull
  private String decodeHttpHeader(String encoded, String encoding) {
    try {
      return URLDecoder.decode(encoded, defaultIfEmpty(encoding, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @CheckReturnValue
  @Nonnull
  public String getFileName(String url) {
    return normalize(trimQuery(FilenameUtils.getName(url)));
  }

  @CheckReturnValue
  @Nonnull
  private String trimQuery(String filenameWithQuery) {
    return filenameWithQuery.contains("?")
      ? left(filenameWithQuery, filenameWithQuery.indexOf('?'))
      : filenameWithQuery;
  }

  @CheckReturnValue
  @Nonnull
  public String normalize(String fileName) {
    return FILENAME_FORBIDDEN_CHARACTERS.matcher(fileName).replaceAll("_").replace(' ', '+');
  }
}
