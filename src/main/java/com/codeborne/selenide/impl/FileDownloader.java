package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDownloader {
  public static FileDownloader instance = new FileDownloader();

  public File download(WebElement element) throws IOException {
    String fileToDownloadLocation = element.getAttribute("href");
    if (fileToDownloadLocation.trim().isEmpty()) {
      throw new IllegalArgumentException("The element does not have href attribute");
    }

    URL fileToDownload = new URL(fileToDownloadLocation);

    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(fileToDownloadLocation);
    HttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(ClientContext.COOKIE_STORE, mimicCookieState());

    HttpResponse response = httpClient.execute(httpGet, localContext);
    System.out.println("DOWNLOAD HEADERS:");
    for (Header header : response.getAllHeaders()) {
      System.out.println(header.getName() + '=' + header.getValue());
    }

    File downloadedFile = new File(Configuration.reportsFolder, getFileName(fileToDownload, response));
    if (!downloadedFile.canWrite()) {
      downloadedFile.setWritable(true);
    }

    try {
      int httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();
      FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
    }
    finally {
      response.getEntity().getContent().close();
    }

    return downloadedFile;
  }

  protected String getFileName(URL fileToDownload, HttpResponse response) {
    for (Header header : response.getAllHeaders()) {
      if ("Content-Disposition".equals(header.getName())) {
        String fileName = getFileNameFromContentDisposition(header.getValue());
        if (fileName != null) {
          return fileName;
        }
      }
    }
    return fileToDownload.getFile().replaceFirst("/|\\\\", "");
  }

  protected String getFileNameFromContentDisposition(String contentDisposition) {
    Matcher regex = Pattern.compile(".*filename=\"?([^\"]*)\"?.*").matcher(contentDisposition);
    return regex.matches() ? regex.replaceFirst("$1") : null;
  }

  protected BasicCookieStore mimicCookieState() {
    Set<Cookie> seleniumCookieSet = WebDriverRunner.getWebDriver().manage().getCookies();
    BasicCookieStore mimicWebDriverCookieStore = new BasicCookieStore();
    for (Cookie seleniumCookie : seleniumCookieSet) {
      BasicClientCookie duplicateCookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
      duplicateCookie.setDomain(seleniumCookie.getDomain());
      duplicateCookie.setSecure(seleniumCookie.isSecure());
      duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
      duplicateCookie.setPath(seleniumCookie.getPath());
      mimicWebDriverCookieStore.addCookie(duplicateCookie);
    }

    return mimicWebDriverCookieStore;
  }
}
