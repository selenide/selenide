---
layout: post
title: "Can PhantomJS take screenshots?"
description: ""
category: 
tags: []
---
{% include JB/setup %}

Many people think that PhantomJS (as a headless browser) cannot take screenshots.

This is not true!

According to [PhantomJS documentation](https://github.com/ariya/phantomjs/wiki/Screen-Capture), it can take screenshots.

Let's try to do that.

```java
import static com.codeborne.selenide.Selenide.*;

public class TestPhantomScreenshot {
  public static void main(String[] args) {
    System.setProperty("browser", "phantomjs");
    open("http://google.com");
    screenshot("google-com-screenshot");
    close();
  }
}
```

It works!

Though, it does not work ideally. Well, screenshots of [google.com]({{ BASE_PATH }}/images/2013/07/google-com-screenshot.png),
[habrahabr.ru]({{ BASE_PATH }}/images/2013/07/habrahabr-ru-screenshot.png) and
[skype.com]({{ BASE_PATH }}/images/2013/07/skype-com-screenshot.png) look good, but screenshot of
[selenide.org]({{ BASE_PATH }}/images/2013/07/selenide-org-screenshot.png) is quite strange.

Does it mean that PhantomJS is not mature enough yet?
I don't know. But at least PhantomJS can do screenshots.

[![google.com]({{ BASE_PATH }}/images/2013/07/google-com-screenshot.thumb.png)]({{ BASE_PATH }}/images/2013/07/google-com-screenshot.png)
[![habrahabr.ru]({{ BASE_PATH }}/images/2013/07/habrahabr-ru-screenshot.thumb.png)]({{ BASE_PATH }}/images/2013/07/habrahabr-ru-screenshot.png)
[![skype.com]({{ BASE_PATH }}/images/2013/07/skype-com-screenshot.thumb.png)]({{ BASE_PATH }}/images/2013/07/skype-com-screenshot.png)
[![selenide.org]({{ BASE_PATH }}/images/2013/07/selenide-org-screenshot.thumb.png)]({{ BASE_PATH }}/images/2013/07/selenide-org-screenshot.png)
