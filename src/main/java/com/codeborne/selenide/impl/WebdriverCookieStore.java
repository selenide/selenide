package com.codeborne.selenide.impl;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.Set;

class WebdriverCookieStore extends BasicCookieStore {
  WebdriverCookieStore(WebDriver webDriver) {
    Set<Cookie> seleniumCookieSet = webDriver.manage().getCookies();
    for (Cookie seleniumCookie : seleniumCookieSet) {
      addCookie(duplicateCookie(seleniumCookie));
    }
  }

  private BasicClientCookie duplicateCookie(Cookie seleniumCookie) {
    BasicClientCookie duplicateCookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
    duplicateCookie.setDomain(seleniumCookie.getDomain());
    duplicateCookie.setAttribute(BasicClientCookie.DOMAIN_ATTR, seleniumCookie.getDomain());
    duplicateCookie.setSecure(seleniumCookie.isSecure());
    duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
    duplicateCookie.setPath(seleniumCookie.getPath());
    return duplicateCookie;
  }
}
