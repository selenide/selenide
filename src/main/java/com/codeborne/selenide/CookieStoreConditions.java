package com.codeborne.selenide;

import com.codeborne.selenide.conditions.cookiestore.CookieWithName;
import com.codeborne.selenide.conditions.cookiestore.CookieWithNameAndValue;

public class CookieStoreConditions {
  public static ObjectCondition<CookieStore> cookie(String name) {
    return new CookieWithName(name);
  }

  public static ObjectCondition<CookieStore> cookie(String name, String value) {
    return new CookieWithNameAndValue(name, value);
  }
}
