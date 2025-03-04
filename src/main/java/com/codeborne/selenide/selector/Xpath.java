package com.codeborne.selenide.selector;

import static org.apache.commons.lang3.StringUtils.repeat;

class Xpath {
  private static final String SPACE_CHARS =
    "\t\n\r\u00a0\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u200C\u200D\u202F\u205F\u2060\u3000";

  static final String NORMALIZE_SPACE_XPATH = "normalize-space(translate(string(.), '%s', '%s'))".formatted(
    SPACE_CHARS, repeat(' ', SPACE_CHARS.length()));
}
