package com.codeborne.selenide;

import com.codeborne.selenide.conditions.clipboard.Content;

/**
 * @since 5.23.0
 */
public class ClipboardConditions {
  public static ObjectCondition<Clipboard> content(String expectedContent) {
    return new Content(expectedContent);
  }
}
