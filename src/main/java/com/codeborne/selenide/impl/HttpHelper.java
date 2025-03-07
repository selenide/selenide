package com.codeborne.selenide.impl;

import org.apache.commons.io.FilenameUtils;
import org.jspecify.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.StringUtils.left;

public class HttpHelper {

  private static final Pattern FILENAME_IN_CONTENT_DISPOSITION_HEADER =
    Pattern.compile(".*filename\\*? *= *\"?((.+)'')?([^\";?]*)\"?(;charset=(.*))?.*", CASE_INSENSITIVE);

  private static final Pattern FILENAME_FORBIDDEN_CHARACTERS =
    Pattern.compile("[#%&{}/\\\\<>*?$!'\":@+`|=]");

  public Optional<String> getFileNameFromContentDisposition(Map<String, String> headers) {
    return getFileNameFromContentDisposition(headers.entrySet());
  }

  public Optional<String> getFileNameFromContentDisposition(Collection<Map.Entry<String, String>> headers) {
    for (Map.Entry<String, String> header : headers) {
      Optional<String> fileName = getFileNameFromContentDisposition(header.getKey(), header.getValue());
      if (fileName.isPresent()) {
        return fileName;
      }
    }
    return Optional.empty();
  }

  public Optional<String> getFileNameFromContentDisposition(String headerName, @Nullable String headerValue) {
    if (!"Content-Disposition".equalsIgnoreCase(headerName) || headerValue == null) {
      return Optional.empty();
    }
    Matcher regex = FILENAME_IN_CONTENT_DISPOSITION_HEADER.matcher(headerValue);
    if (!regex.matches()) {
      return Optional.empty();
    }
    String fileNamePart = regex.replaceFirst("$3");
    String encodingPart = defaultIfEmpty(regex.replaceFirst("$2"), regex.replaceFirst("$5"));
    String filename = decodeHttpHeader(fileNamePart, encodingPart);
    try {
      return Optional.of(new String(Base64.getDecoder().decode(filename), UTF_8));
    }
    catch (IllegalArgumentException notBase64Encoded) {
      return Optional.of(filename);
    }
  }

  private String decodeHttpHeader(String encoded, String encoding) {
    try {
      return URLDecoder.decode(encoded, defaultIfEmpty(encoding, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public String getFileName(String url) {
    return normalize(trimQuery(FilenameUtils.getName(url)));
  }

  private String trimQuery(String filenameWithQuery) {
    return filenameWithQuery.contains("?")
      ? left(filenameWithQuery, filenameWithQuery.indexOf('?'))
      : filenameWithQuery;
  }

  public String normalize(String fileName) {
    return FILENAME_FORBIDDEN_CHARACTERS.matcher(fileName).replaceAll("_").replace(' ', '+');
  }
}
