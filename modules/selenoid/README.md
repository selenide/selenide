Selenide Selenoid plugin
================================

A [Selenide](https://selenide.org) extension for working with Selenoid.

## Why it's needed?

Most Selenide features work with Selenoid out of the box.  
There are only a few things that might not work, e.g. downloading files and working with clipboard.

This problem is addressed by `selenide-selenoid` plugin: _it can get files from Selenoid container_.

## Features
Currently, the plugin supports the following features:
* File download
* Access to clipboard (get & set & verify clipboard contents)
* BasicAuth

We are going to implement more features like reading logs and videos from Selenoid.

## Usage
1. Import `com.codeborne:selenide-selenoid:7.12.1`
2. Use these methods as usually:
   - `$.download(file().withMethod(FOLDER)); // or CDP or HTTPGET or PROXY`
   - `clipboard().setText("Selenoid Shadow");`
   - `clipboard().shouldHave(content("Selenoid Shadow"));`

## How it works?
When you call `$("a#report").download()` method, `selenide-selenoid` plugin:
1. clicks the element,
2. lets the browser download file to its default location,
3. uses [Selenoid API](https://aerokube.com/selenoid/latest/#_accessing_files_downloaded_with_browser) to get the downloaded file.  


## Deprecation notice

[Selenoid](https://github.com/aerokube/selenoid) project is not supported anymore.
So `selenide-selenoid` plugin is also deprecated.
You can use `selenide-moon` or `selenide-grid` instead.

Or even better, just run the browser locally (on the same machine with test). It's the fastest and stablest way.
