package com.codeborne.selenide;

enum WhitespaceHandling {
  /**
   * do not ignore whitespaces
   * e.g. "hello world" != "hello  world"
   */
  PRESERVE_WHITESPACES,

  /**
   * collapse multiple spaces into one
   * e.g. "hello world" == "hello      world"
   */
  TRIM_WHITESPACES,

  /**
   * ignore all whitespaces
   * e.g. "hello world" == "helloworld" == "hello       world" == " h e l l o w o r l d "
   */
  @SuppressWarnings("SpellCheckingInspection")
  IGNORE_WHITESPACES
}
