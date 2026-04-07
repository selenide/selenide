# Selenide File Download

## Basic download
```java
File file = $("a.download-link").download();
```

## Download methods

### HTTPGET (default)
Sends HTTP GET with browser cookies. Fast, but only works with direct links.
```java
File file = $("a").download(DownloadOptions.using(HTTPGET));
```

### FOLDER
Clicks the element and waits for a new file in the downloads folder. Works with any download trigger.
```java
File file = $("button.export").download(DownloadOptions.using(FOLDER));
```

### PROXY
Uses a built-in proxy to intercept the response. Requires `Configuration.proxyEnabled = true`.
```java
Configuration.proxyEnabled = true;
File file = $("a").download(DownloadOptions.using(PROXY));
```

### CDP (Chrome DevTools Protocol)
Uses Chrome DevTools to intercept downloads. Chrome-only.
```java
File file = $("a").download(DownloadOptions.using(CDP));
```

## Download with options
```java
File file = $("a").download(DownloadOptions.using(FOLDER)
  .withTimeout(Duration.ofSeconds(30))
  .withFilter(withExtension("pdf"))
  .withName("report.pdf"));
```

## File filters
```java
withExtension("pdf")         // by file extension
withName("report.pdf")       // by exact name
withNameMatching(".*\\.csv") // by regex
```
