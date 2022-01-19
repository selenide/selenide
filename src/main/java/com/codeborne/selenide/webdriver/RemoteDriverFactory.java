package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.TracedCommandExecutor;
import org.openqa.selenium.remote.http.ClientConfig;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.tracing.TracedHttpClient;
import org.openqa.selenium.remote.tracing.Tracer;
import org.openqa.selenium.remote.tracing.opentelemetry.OpenTelemetryTracer;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static com.codeborne.selenide.webdriver.HttpClientTimeouts.defaultConnectTimeout;
import static com.codeborne.selenide.webdriver.HttpClientTimeouts.defaultReadTimeout;
import static java.util.Collections.emptyMap;

@ParametersAreNonnullByDefault
public class RemoteDriverFactory {
  public WebDriver create(Config config, MutableCapabilities capabilities) {
    try {
      TracedCommandExecutor tracedCommandExecutor = createExecutor(config, defaultReadTimeout, defaultConnectTimeout);
      RemoteWebDriver webDriver = new RemoteWebDriver(tracedCommandExecutor, capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    }
    catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + config.remote(), e);
    }
  }

  @Nonnull
  @CheckReturnValue
  private TracedCommandExecutor createExecutor(Config config, Duration readTimeout, Duration connectTimeout) throws MalformedURLException {
    ClientConfig clientConfig = ClientConfig.defaultConfig()
      .baseUrl(new URL(config.remote()))
      .readTimeout(readTimeout)
      .connectionTimeout(connectTimeout);

    Tracer tracer = OpenTelemetryTracer.getInstance();

    CommandExecutor httpCommandExecutor = new HttpCommandExecutor(emptyMap(), clientConfig,
      new TracedHttpClient.Factory(tracer, HttpClient.Factory.createDefault()));

    return new TracedCommandExecutor(httpCommandExecutor, tracer);
  }
}
