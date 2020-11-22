Selenide Selenoid plugin
================================

A Selenide extension for working with Selenoid

## Features
Currently the plugin supports only one feature:
* File download 

We are going to implement more features, like using clipboard, reading logs and videos etc.

## Usage
1. Import `org.selenide:selenide-selenoid:1.0.0`
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

Register [github issues](https://github.com/selenide/selenide-selenoid/issues), write to 
[![chat](https://img.shields.io/badge/chat-green.svg)](https://gitter.im/codeborne/selenide?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
 or
[![чат](https://img.shields.io/badge/%D1%87%D0%B0%D1%82-green.svg)](https://gitter.im/codeborne/selenide-ru?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge).

[![Follow](https://img.shields.io/twitter/follow/selenide.svg?style=social&label=Follow)](https://twitter.com/selenide)