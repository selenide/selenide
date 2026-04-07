# Selenide Configuration

## Common settings
```java
Configuration.browser = "chrome";          // chrome, firefox, edge, safari
Configuration.headless = true;             // headless mode
Configuration.baseUrl = "https://app.com"; // prepended to relative URLs
Configuration.timeout = 4000;              // default wait timeout (ms)
Configuration.pageLoadTimeout = 30_000;    // page load timeout (ms)
```

## Browser size
```java
Configuration.browserSize = "1920x1080";
Configuration.startMaximized = true;
```

## Screenshots and reports
```java
Configuration.reportsFolder = "build/reports/tests";
Configuration.screenshots = true;
Configuration.savePageSource = false;
```

## File download
```java
Configuration.fileDownload = FileDownloadMode.FOLDER;   // HTTPGET, FOLDER, PROXY, CDP
Configuration.downloadsFolder = "build/downloads";
```

## Remote WebDriver
```java
Configuration.remote = "http://localhost:4444/wd/hub";
```

## Proxy
```java
Configuration.proxyEnabled = true;
Configuration.proxyHost = "localhost";
Configuration.proxyPort = 8080;
```

## Programmatic config (non-static)
```java
SelenideConfig config = new SelenideConfig()
  .browser("chrome")
  .headless(true)
  .baseUrl("https://app.com")
  .timeout(6000);
SelenideDriver driver = new SelenideDriver(config);
driver.open("/login");
driver.$("h1").shouldHave(text("Login"));
```
