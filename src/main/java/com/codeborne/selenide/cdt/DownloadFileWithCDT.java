package com.codeborne.selenide.cdt;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.Downloader;
import com.codeborne.selenide.impl.Downloads;
import com.codeborne.selenide.impl.HttpHelper;
import com.codeborne.selenide.impl.Waiter;
import com.codeborne.selenide.impl.WebElementSource;
import com.codeborne.selenide.proxy.DownloadedFile;
import com.github.kklisura.cdt.protocol.commands.Network;
import com.github.kklisura.cdt.protocol.types.network.Request;
import com.github.kklisura.cdt.protocol.types.network.Response;
import com.github.kklisura.cdt.protocol.types.network.ResponseBody;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.impl.ChromeServiceImpl;
import com.github.kklisura.cdt.services.types.ChromeTab;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ParametersAreNonnullByDefault
public class DownloadFileWithCDT {
  private static final Logger log = LoggerFactory.getLogger(DownloadFileWithCDT.class);

  private final Downloader downloader;
  private final Waiter waiter;
  private final HttpHelper httpHelper = new HttpHelper();

  DownloadFileWithCDT(Downloader downloader, Waiter waiter) {
    this.downloader = downloader;
    this.waiter = waiter;
  }

  public DownloadFileWithCDT() {
    this(new Downloader(), new Waiter());
  }

  @CheckReturnValue
  @Nonnull
  public File download(WebElementSource anyClickableElement,
                       WebElement clickable, long timeout,
                       FileFilter fileFilter) throws FileNotFoundException {
    return clickAndInterceptFileByCDT(anyClickableElement, clickable, timeout, fileFilter);
  }

  @CheckReturnValue
  @Nonnull
  private File clickAndInterceptFileByCDT(WebElementSource anyClickableElement, WebElement clickable,
                                          long timeout,
                                          FileFilter fileFilter) throws FileNotFoundException {
    Config config = anyClickableElement.driver().config();
    WebDriver webDriver = anyClickableElement.driver().getWebDriver();
    String currentWindowHandle = webDriver.getWindowHandle();
    Set<String> currentWindows = webDriver.getWindowHandles();

    Downloads downloads = new Downloads();
    Map<String, Request> requests = new ConcurrentHashMap<>();
    Map<String, Response> responses = new ConcurrentHashMap<>();
    int debuggerPort = getDebuggerPort(anyClickableElement.driver());
    ChromeDevToolsService cdpService = getChromeDevToolsService(debuggerPort);
    Network network = cdpService.getNetwork();

    network.onRequestWillBeSent(e -> {
      log.info("Starting request {} {} {} {}", e.getRequestId(), e.getType(), e.getDocumentURL(), e.getLoaderId());
      requests.put(e.getRequestId(), e.getRequest());
    });

    network.onResponseReceived(
      e -> {
        Response response = e.getResponse();
        log.info("Finished request {} {} {} -> {} {} length:{} fromDiskCache:{}, fromPrefetchCache:{}",
          e.getRequestId(), e.getType(), response.getMimeType(),
          response.getStatus(), response.getUrl(), response.getEncodedDataLength(),
          response.getFromDiskCache(), response.getFromPrefetchCache());
        responses.put(e.getRequestId(), response);
      });
    network.onLoadingFailed(e -> {
      log.info("Loading failed: {} type={} cause={} reason={} cancelled={}", e.getRequestId(), e.getType(),
        e.getErrorText(), e.getBlockedReason(), e.getCanceled());
    });
    network.onDataReceived(e -> {
      log.info("On data received: {}", e.getRequestId());
    });

    network.onLoadingFinished(e -> {
      log.info("Loading finished {}", e.getRequestId());
      Request request = requests.get(e.getRequestId());
      Response response = responses.get(e.getRequestId());
      if (request != null) {
        log.info("Finished request {} {} {} -> {} {}, length: {}", e.getRequestId(), response.getMimeType(), request.getUrl(),
          response.getStatus(), response.getUrl(), e.getEncodedDataLength());
        ResponseBody body = network.getResponseBody(e.getRequestId());
        Map<String, String> headers = toMap(response.getHeaders());
        DownloadedFile file = new DownloadedFile(saveFile(config, request, body, headers), headers);
        downloads.add(file);
      }
    });
    network.enable();

    try {
      waiter.wait(downloads, new PreviousDownloadsCompleted(), timeout, config.pollingInterval());

      downloads.clear();
      clickable.click();

      waiter.wait(downloads, new HasDownloads(fileFilter), timeout, config.pollingInterval());
      return firstDownloadedFile(anyClickableElement, downloads, timeout, fileFilter);
    }
    catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
    finally {
      network.disable();
      closeNewWindows(webDriver, currentWindowHandle, currentWindows);
    }
  }

  private Map<String, String> toMap(Map<String, Object> headers) {
    Map<String, String> map = new HashMap<>();
    for (Map.Entry<String, Object> header : headers.entrySet()) {
      map.put(header.getKey().toLowerCase(), header.getValue().toString());
    }
    return map;
  }

  private File saveFile(Config config, Request request, ResponseBody response, Map<String, String> headers) {
    File file = downloader.prepareTargetFile(config, getFileName(request.getUrl(), headers));
    try {
      if (response.getBase64Encoded()) {
        byte[] binaryContent = Base64.getDecoder().decode(response.getBody());
        writeByteArrayToFile(file, binaryContent);
      } else {
        writeStringToFile(file, response.getBody(), UTF_8);
      }
    } catch (IOException e) {
      log.error("Failed to save downloaded file to {} for url {}", file.getAbsolutePath(), request.getUrl(), e);
    }
    return file;
  }

  @CheckReturnValue
  @Nonnull
  private String getFileName(String url, Map<String, String> headers) {
    return httpHelper.getFileNameFromContentDisposition(headers)
      .map(httpHelper::normalize)
      .orElseGet(() -> {
        log.info("Cannot extract file name from http headers. Found headers: ");
        for (Map.Entry<String, String> header : headers.entrySet()) {
          log.info("{}={}", header.getKey(), header.getValue());
        }

        String fileNameFromUrl = httpHelper.getFileName(url);
        return isNotBlank(fileNameFromUrl) ? fileNameFromUrl : downloader.randomFileName();
      });
  }

  private void closeNewWindows(WebDriver webDriver, String currentWindowHandle, Set<String> currentWindows) {
    Set<String> windowHandles = webDriver.getWindowHandles();
    if (windowHandles.size() != currentWindows.size()) {
      Set<String> newWindows = new HashSet<>(windowHandles);
      newWindows.removeAll(currentWindows);

      log.info("File has been opened in a new window, let's close {} new windows", newWindows.size());

      for (String newWindow : newWindows) {
        log.info("  Let's close {}", newWindow);
        try {
          webDriver.switchTo().window(newWindow);
          webDriver.close();
        }
        catch (NoSuchWindowException windowHasBeenClosedMeanwhile) {
          log.info("  Failed to close {}: {}", newWindow, Cleanup.of.webdriverExceptionMessage(windowHasBeenClosedMeanwhile));
        }
        catch (Exception e) {
          log.warn("  Failed to close {}", newWindow, e);
        }
      }
      webDriver.switchTo().window(currentWindowHandle);
    }
  }

  @ParametersAreNonnullByDefault
  private static class HasDownloads implements Predicate<Downloads> {
    private final FileFilter fileFilter;

    private HasDownloads(FileFilter fileFilter) {
      this.fileFilter = fileFilter;
    }

    @Override
    public boolean test(Downloads downloads) {
      return !downloads.files(fileFilter).isEmpty();
    }
  }

  @ParametersAreNonnullByDefault
  private static class PreviousDownloadsCompleted implements Predicate<Downloads> {
    private int downloadsCount = -1;

    @Override
    public boolean test(Downloads downloads) {
      try {
        return downloadsCount == downloads.size();
      }
      finally {
        downloadsCount = downloads.size();
      }
    }
  }

  @CheckReturnValue
  @Nonnull
  private File firstDownloadedFile(WebElementSource anyClickableElement,
                                   Downloads downloads,
                                   long timeout,
                                   FileFilter fileFilter) throws FileNotFoundException {
    if (downloads.size() == 0) {
      throw new FileNotFoundException("Failed to download file " + anyClickableElement +
        " in " + timeout + " ms.\n" + downloads.filesAsString());
    }

    log.info(downloads.filesAsString());
    // log.info("Just in case, all intercepted responses: {}", filter.responsesAsString());

    return downloads.firstMatchingFile(fileFilter)
      .orElseThrow(() -> new FileNotFoundException(String.format("Failed to download file %s in %d ms.%s",
        anyClickableElement, timeout, fileFilter.description())
        )
      ).getFile();
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  private ChromeDevToolsService getChromeDevToolsService(int debuggerPort) {
    ChromeService chromeService = new ChromeServiceImpl(debuggerPort);
    ChromeTab pageTab = chromeService.getTabs().stream().filter(tab -> tab.getType().equals("page")).findFirst().get();
    return chromeService.createDevToolsService(pageTab);
  }

  @SuppressWarnings("unchecked")
  private int getDebuggerPort(Driver driver) {
    Capabilities caps = ((HasCapabilities) driver.getWebDriver()).getCapabilities();
    String debuggerAddress = (String) ((Map<String, Object>) caps.getCapability("goog:chromeOptions")).get("debuggerAddress");
    return Integer.parseInt(debuggerAddress.split(":")[1]);
  }
}
