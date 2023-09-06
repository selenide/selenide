Selenide Selenoid plugin
================================

A [Selenide](https://selenide.org) extension for working with Selenoid

## Why it's needed?

Most Selenide features work with Selenoid out of the box.  
There are only a few things that might not work: proxy and downloading files. 
1. Selenide proxy generally works with Selenoid, except one case: when tests and Selenoid are run on different machines, 
and "test machine" is not accessible from "selenoid machine". 
2. Downloading files: Selenide has 3 methods for downloading files: `HTTPGET`, `PROXY` and `FOLDER`.   
   * `HTTPGET` and `PROXY` work in Selenoid as usually
   * `FOLDER` doesn't work in Selenoid because the downloads folder is inside the container.   

This problem is addressed by `selenide-selenoid` plugin: _it can get files from Selenoid container_. 

## Features
Currently, the plugin supports the following features:
* File download 
* Access to clipboard  (get & set & verify clipboard contents)
* BasicAuth

We are going to implement more features like reading logs and videos etc.

## Usage
1. Import `com.codeborne:selenide-selenoid:6.18.0`
2. Set `Configuration.fileDownload = FOLDER;`
3. Use method `$.download()` as usually.

## How it works?
When you call `$("a#report").download()` method, Selenide-selenoid plugin
1. clicks the element,
2. lets the browser download file to its default location, 
3. uses [Selenoid API](https://aerokube.com/selenoid/latest/#_accessing_files_downloaded_with_browser) to get the downloaded file.  

## Sample project

This is a sample project using `selenide-selenoid` plugin:  
* [github/selenide-examples/selenoid-sample](https://github.com/selenide-examples/selenoid-sample)

## Feedback
_Feel free to share your feedback!_

Register [GitHub issues](https://github.com/selenide/selenide/issues), write to 
[![chat](https://img.shields.io/badge/chat-green.svg)](https://gitter.im/codeborne/selenide?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
 or
[![чат](https://img.shields.io/badge/%D1%87%D0%B0%D1%82-green.svg)](https://gitter.im/codeborne/selenide-ru?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge).

[![Follow](https://img.shields.io/twitter/follow/selenide.svg?style=social&label=Follow)](https://twitter.com/selenide)
