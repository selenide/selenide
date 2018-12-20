package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.TimeoutException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.openqa.selenium.WebElement;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.logging.Logger;

import static com.codeborne.selenide.impl.Describe.describe;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.http.client.protocol.HttpClientContext.COOKIE_STORE;

public class DownloadFileWithHttpRequest {
  private static final Logger log = Logger.getLogger(DownloadFileWithHttpRequest.class.getName());

  protected boolean ignoreSelfSignedCerts = true;

  private final Downloader downloader;
  private HttpHelper httpHelper = new HttpHelper();

  public DownloadFileWithHttpRequest() {
    this(new Downloader());
  }

  DownloadFileWithHttpRequest(Downloader downloader) {
    this.downloader = downloader;
  }

  public File download(Driver driver, WebElement element, long timeout) throws IOException {
    String fileToDownloadLocation = element.getAttribute("href");
    if (fileToDownloadLocation == null || fileToDownloadLocation.trim().isEmpty()) {
      throw new IllegalArgumentException("The element does not have href attribute: " + describe(driver, element));
    }

    return download(driver, fileToDownloadLocation, timeout);
  }

  public File download(Driver driver, String relativeOrAbsoluteUrl, long timeout) throws IOException {
    String url = makeAbsoluteUrl(driver.config(), relativeOrAbsoluteUrl);
    HttpResponse response = executeHttpRequest(driver, url, timeout);

    if (response.getStatusLine().getStatusCode() >= 500) {
      throw new RuntimeException("Failed to download file " +
        url + ": " + response.getStatusLine());
    }
    if (response.getStatusLine().getStatusCode() >= 400) {
      throw new FileNotFoundException("Failed to download file " +
        url + ": " + response.getStatusLine());
    }

    String fileName = getFileName(url, response);
    File downloadedFile = downloader.prepareTargetFile(driver.config(), fileName);
    return saveFileContent(response, downloadedFile);
  }

  String makeAbsoluteUrl(Config config, String relativeOrAbsoluteUrl) {
    return relativeOrAbsoluteUrl.startsWith("/") ? config.baseUrl() + relativeOrAbsoluteUrl : relativeOrAbsoluteUrl;
  }

  protected HttpResponse executeHttpRequest(Driver driver, String fileToDownloadLocation, long timeout) throws IOException {
    CloseableHttpClient httpClient = ignoreSelfSignedCerts ? createTrustingHttpClient() : createDefaultHttpClient();
    HttpGet httpGet = new HttpGet(fileToDownloadLocation);
    configureHttpGet(httpGet, timeout);
    addHttpHeaders(driver, httpGet);
    try {
      return httpClient.execute(httpGet, createHttpContext(driver));
    }
    catch (SocketTimeoutException timeoutException) {
      throw new TimeoutException("Failed to download " + fileToDownloadLocation + " in " + timeout + " ms.", timeoutException);
    }
  }

  protected void configureHttpGet(HttpGet httpGet, long timeout) {
    httpGet.setConfig(RequestConfig.custom()
        .setConnectTimeout((int) timeout)
        .setSocketTimeout((int) timeout)
        .setConnectionRequestTimeout((int) timeout)
        .setRedirectsEnabled(true)
        .setCircularRedirectsAllowed(true)
        .setMaxRedirects(20)
        .setCookieSpec(CookieSpecs.STANDARD)
        .build()
    );
  }

  protected CloseableHttpClient createDefaultHttpClient() {
    return HttpClients.createDefault();
  }

  private static class TrustAllStrategy implements TrustStrategy {
    @Override
    public boolean isTrusted(X509Certificate[] arg0, String arg1) {
      return true;
    }
  }

  /**
   configure HttpClient to ignore self-signed certs
   as described here: http://literatejava.com/networks/ignore-ssl-certificate-errors-apache-httpclient-4-4/
  */
  protected CloseableHttpClient createTrustingHttpClient() throws IOException {
    try {
      HttpClientBuilder builder = HttpClientBuilder.create();
      SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustAllStrategy()).build();
      builder.setSSLContext(sslContext);

      HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

      SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
      Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
          .register("http", PlainConnectionSocketFactory.getSocketFactory())
          .register("https", sslSocketFactory)
          .build();

      PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
      builder.setConnectionManager(connMgr);
      return builder.build();
    }
    catch (Exception e) {
      throw new IOException(e);
    }
  }

  protected HttpContext createHttpContext(Driver driver) {
    HttpContext localContext = new BasicHttpContext();
    if (driver.hasWebDriverStarted()) {
      localContext.setAttribute(COOKIE_STORE, new WebdriverCookieStore(driver.getWebDriver()));
    }
    return localContext;
  }

  protected void addHttpHeaders(Driver driver, HttpGet httpGet) {
    if (driver.hasWebDriverStarted()) {
      httpGet.setHeader("User-Agent", driver.getUserAgent());
    }
  }

  protected String getFileName(String fileToDownloadLocation, HttpResponse response) {
    for (Header header : response.getAllHeaders()) {
      Optional<String> fileName = httpHelper.getFileNameFromContentDisposition(header.getName(), header.getValue());
      if (fileName.isPresent()) {
        return fileName.get();
      }
    }

    log.info("Cannot extract file name from http headers. Found headers: ");
    for (Header header : response.getAllHeaders()) {
      log.info(header.getName() + '=' + header.getValue());
    }

    String fullFileName = FilenameUtils.getName(fileToDownloadLocation);
    return isBlank(fullFileName) ? downloader.randomFileName() : trimQuery(fullFileName);
  }

  private String trimQuery(String fullFileName) {
    return fullFileName.contains("?")
      ? StringUtils.left(fullFileName, fullFileName.indexOf("?"))
      : fullFileName;
  }

  protected File saveFileContent(HttpResponse response, File downloadedFile) throws IOException {
    copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
    return downloadedFile;
  }
}
