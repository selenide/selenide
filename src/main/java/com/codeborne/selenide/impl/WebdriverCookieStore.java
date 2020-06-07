package com.codeborne.selenide.impl;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

import static org.apache.hc.client5.http.cookie.Cookie.DOMAIN_ATTR;

@ParametersAreNonnullByDefault
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
    duplicateCookie.setAttribute(DOMAIN_ATTR, seleniumCookie.getDomain());
    duplicateCookie.setSecure(seleniumCookie.isSecure());
    duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
    duplicateCookie.setPath(seleniumCookie.getPath());
    return duplicateCookie;
  }
}
