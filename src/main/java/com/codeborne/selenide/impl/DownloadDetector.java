package com.codeborne.selenide.impl;

import com.codeborne.selenide.proxy.DownloadedFile;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Sort all downloaded files by "likeness" to be the right download.
 * <p>
 * 1. Response with "Content-Disposition" is most likely the right download.
 * 2. Response with type "text/html", "text/plain", "text/css", "text/javascript", "application/json"
 *    are less likely the right download.
 * 3. Latest file wins
 * 4. The first file (alphabetically) wins
 * </p>
 */
@ParametersAreNonnullByDefault
public class DownloadDetector implements Comparator<DownloadedFile>, Serializable {
  private static final Set<String> LOW_RANK_CONTENT_TYPES = new HashSet<>(asList(
    "text/html", "text/plain", "text/css", "text/javascript", "application/json"
  ));

  @Override
  @CheckReturnValue
  public int compare(DownloadedFile file1, DownloadedFile file2) {
    int result = Boolean.compare(file2.hasContentDispositionHeader(), file1.hasContentDispositionHeader());
    if (result == 0) {

      boolean isHtmlOrCss1 = LOW_RANK_CONTENT_TYPES.contains(file1.getContentType());
      boolean isHtmlOrCss2 = LOW_RANK_CONTENT_TYPES.contains(file2.getContentType());
      result = Boolean.compare(isHtmlOrCss1, isHtmlOrCss2);

      if (result == 0) {
        result = Long.compare(file1.getFile().lastModified(), file2.getFile().lastModified());
        if (result == 0) {
          result = file1.getFile().getName().compareTo(file2.getFile().getName());
        }
      }
    }

    return result;
  }
}
