Selenide Moon plugin
================================

A [Selenide](https://selenide.org) extension for working with [Moon](https://aerokube.com/moon/)

## Why it's needed?

Most Selenide features work with Moon out of the box.  
There are only a few things that might not work, e.g. downloading files and working with clipboard. 

This problem is addressed by `selenide-moon` plugin: _it can get files from Moon container_. 

## Features
Currently, the plugin supports the following features:
* File download 
* Access to clipboard  (get & set & verify clipboard contents)
* BasicAuth

We are going to implement more features like reading logs and videos etc.

## Usage
1. Import `com.codeborne:selenide-moon:7.9.1`
2. Set `Configuration.fileDownload = FOLDER;`
3. Use method `$.download()` as usually.

## How it works?
When you call `$("a#report").download()` method, Selenide-moon plugin
1. clicks the element,
2. lets the browser download file to its default location, 
3. uses [Moon API](https://aerokube.com/moon/latest/#accessing-downloaded-files) to get the downloaded file.  
