package com.codeborne.selenide;

import com.codeborne.selenide.conditions.clipboard.Content;

public class ClipboardConditions {
  public static ObjectCondition<Clipboard> content(String expectedContent) {
    return new Content(expectedContent);
  }
}
