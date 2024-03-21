package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.FileNotDownloadedError;
import com.codeborne.selenide.files.DownloadedFile;
import com.codeborne.selenide.files.FileFilter;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultRedirectStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntityContainer;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Stream;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNullElse;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.hc.client5.http.protocol.HttpClientContext.COOKIE_STORE;

@ParametersAreNonnullByDefault
public class DownloadFileWithHttpRequest {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileWithHttpRequest.class);
  private final ElementDescriber describe = inject(ElementDescriber.class);

  protected boolean ignoreSelfSignedCerts = true;

  private final Downloader downloader;
  private final HttpHelper httpHelper = new HttpHelper();

  public DownloadFileWithHttpRequest() {
    this(new Downloader());
  }

  DownloadFileWithHttpRequest(Downloader downloader) {
    this.downloader = downloader;
  }

  @CheckReturnValue
  @Nonnull
  public File download(Driver driver, WebElement element, long timeout, FileFilter fileFilter) {
    String fileToDownloadLocation = element.getAttribute("href");
    if (fileToDownloadLocation == null || fileToDownloadLocation.trim().isEmpty()) {
      String link = "https://selenide.org/javadoc/current/com/codeborne/selenide/FileDownloadMode.html";
      throw new IllegalArgumentException((
        "The element does not have \"href\" attribute: %s, so method HTTPGET cannot download the file.%n" +
        "Please try another download method: FOLDER, CDP or PROXY.%n" +
        "Read more about possible download methods: %s"
      ).formatted(describe.fully(driver, element), link).trim());
    }

    return download(driver, fileToDownloadLocation, timeout, fileFilter);
  }

  @CheckReturnValue
  @Nonnull
  public File download(Driver driver, URI url, long timeout, FileFilter fileFilter) {
    return download(driver, url.toASCIIString(), timeout, fileFilter);
  }

  @CheckReturnValue
  @Nonnull
  public File download(Driver driver, String relativeOrAbsoluteUrl, long timeout, FileFilter fileFilter) {
    String url = makeAbsoluteUrl(driver.config(), relativeOrAbsoluteUrl);

    MemorizingRedirectStrategy redirectStrategy = new MemorizingRedirectStrategy();
    try (CloseableHttpClient httpClient = createHttpClient(redirectStrategy)) {
      Resource resource = parseUrl(url);
      HttpGet httpGet = new HttpGet(resource.uri());
      configureHttpGet(httpGet, timeout);
      addHttpHeaders(driver, httpGet, resource.credentials());
      return httpClient.execute(httpGet, createHttpContext(driver), response -> {
          String responseUrl = requireNonNullElse(redirectStrategy.lastRedirectUrl, url);
          return handleResponse(driver, timeout, fileFilter, responseUrl, response);
        }
      );
    }
    catch (IOException e) {
      throw new FileNotDownloadedError(driver, "Failed to download " + url + " in " + timeout + " ms.", timeout, e);
    }
  }

  @Nonnull
  private CloseableHttpClient createHttpClient(MemorizingRedirectStrategy redirectStrategy) throws IOException {
    return ignoreSelfSignedCerts ? createTrustingHttpClient(redirectStrategy) : createDefaultHttpClient(redirectStrategy);
  }

  @Nonnull
  private File handleResponse(Driver driver, long timeout, FileFilter fileFilter, String url,
                              ClassicHttpResponse response) throws IOException {
    if (response.getCode() >= 500) {
      throw new RuntimeException("Failed to download file " + url + ": " + response);
    }
    if (response.getCode() >= 400) {
      throw new FileNotDownloadedError(driver, "Failed to download file " + url + ": " + response, timeout);
    }

    String fileName = getFileName(url, response);
    File downloadedFile = downloader.prepareTargetFile(driver.config(), fileName);
    saveContentToFile(response, downloadedFile);

    if (!fileFilter.match(new DownloadedFile(downloadedFile, emptyMap()))) {
      String message = String.format("Failed to download file from %s in %d ms.%s;%n actually downloaded: %s",
        url, timeout, fileFilter.description(), downloadedFile.getAbsolutePath());
      throw new FileNotDownloadedError(driver, message, timeout);
    }
    return downloadedFile;
  }

  @CheckReturnValue
  @Nonnull
  String makeAbsoluteUrl(Config config, String relativeOrAbsoluteUrl) {
    return relativeOrAbsoluteUrl.startsWith("/") ? config.baseUrl() + relativeOrAbsoluteUrl : relativeOrAbsoluteUrl;
  }

  static Resource parseUrl(String urlWithCredentials) throws IOException {
    try {
      URI uri = new URI(urlWithCredentials);
      return (uri.getUserInfo() == null) ? new Resource(uri, "") :
        new Resource(new URI(urlWithCredentials.replace(uri.getRawUserInfo() + '@', "")), uri.getUserInfo());
    }
    catch (URISyntaxException invalidUrl) {
      throw new IOException(String.format("Failed to download file from %s", urlWithCredentials), invalidUrl);
    }
  }

  @ParametersAreNonnullByDefault
  record Resource(URI uri, String credentials) {
  }

  protected void configureHttpGet(HttpGet httpGet, long timeout) {
    httpGet.setConfig(RequestConfig.custom()
      .setConnectTimeout(timeout, MILLISECONDS)
      .setConnectionRequestTimeout(timeout, MILLISECONDS)
      .setResponseTimeout(timeout, MILLISECONDS)
      .setRedirectsEnabled(true)
      .setCircularRedirectsAllowed(true)
      .setMaxRedirects(20)
      .build()
    );
  }

  @CheckReturnValue
  @Nonnull
  protected CloseableHttpClient createDefaultHttpClient(RedirectStrategy redirectStrategy) {
    return HttpClients.custom()
      .setRedirectStrategy(redirectStrategy)
      .build();
  }

  @ParametersAreNonnullByDefault
  static class MemorizingRedirectStrategy extends DefaultRedirectStrategy {
    @Nullable
    private String lastRedirectUrl;

    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException {
      URI redirectUrl = super.getLocationURI(request, response, context);
      if (redirectUrl != null) {
        lastRedirectUrl = redirectUrl.toString();
      }
      return redirectUrl;
    }
  }

  @ParametersAreNonnullByDefault
  private static class TrustAllStrategy implements TrustStrategy {
    @Override
    public boolean isTrusted(X509Certificate[] arg0, String arg1) {
      return true;
    }
  }

  /**
   * configure HttpClient to ignore self-signed certs
   * as described here: <a href="https://literatejava.com/networks/ignore-ssl-certificate-errors-apache-httpclient-4-4/">...</a>
   */
  @CheckReturnValue
  @Nonnull
  protected CloseableHttpClient createTrustingHttpClient(RedirectStrategy redirectStrategy) throws IOException {
    try {
      HttpClientBuilder builder = HttpClientBuilder.create();
      builder.setRedirectStrategy(redirectStrategy);

      SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustAllStrategy()).build();

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
    catch (GeneralSecurityException e) {
      throw new IOException(e);
    }
  }

  @CheckReturnValue
  @Nonnull
  protected HttpContext createHttpContext(Driver driver) {
    HttpContext localContext = new BasicHttpContext();
    if (driver.hasWebDriverStarted()) {
      localContext.setAttribute(COOKIE_STORE, new WebdriverCookieStore(driver.getWebDriver()));
    }
    return localContext;
  }

  protected void addHttpHeaders(Driver driver, HttpGet httpGet, String credentials) {
    if (driver.hasWebDriverStarted()) {
      httpGet.setHeader("User-Agent", driver.getUserAgent());
    }
    if (!credentials.isEmpty()) {
      httpGet.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(UTF_8)));
    }
  }

  @CheckReturnValue
  @Nonnull
  protected String getFileName(String fileToDownloadLocation, HttpResponse response) {
    for (Header header : response.getHeaders()) {
      Optional<String> fileName = httpHelper.getFileNameFromContentDisposition(header.getName(), header.getValue());
      if (fileName.isPresent()) {
        return httpHelper.normalize(fileName.get());
      }
    }

    log.info("Cannot extract file name for {}. Found headers: {}", fileToDownloadLocation, headersToString(response));

    String fileNameFromUrl = httpHelper.getFileName(fileToDownloadLocation);
    String result = isNotBlank(fileNameFromUrl) ? fileNameFromUrl : downloader.randomFileName();
    log.info("Generated file name for {}: {}", fileToDownloadLocation, result);
    return result;
  }

  @Nonnull
  private String headersToString(HttpResponse response) {
    return Stream.of(response.getHeaders()).map(h -> h.getName() + "=" + h.getValue()).collect(joining(", "));
  }

  protected void saveContentToFile(HttpEntityContainer response, File downloadedFile) throws IOException {
    copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
  }
}
